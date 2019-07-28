package com.ab.example.testerapp.service;

import com.ab.example.testerapp.conf.TesterAppConfiguration;
import com.ab.example.testerapp.entity.OutlierMessage;
import com.expedia.www.aurora.alertprocessor.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import io.dropwizard.lifecycle.Managed;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class TesterAppService implements Managed {

    private static final Logger LOGGER = LoggerFactory.getLogger(TesterAppService.class);

    private Client client;
    private TesterAppConfiguration configuration;
    private final ObjectMapper mapper = new ObjectMapper();

    private List<AlertSubscriptionGroup> alertSubscriptionGroups;
    private List<AlgorithmDefinition> algorithmDefinitions;
    private List<AlertType> alertTypes;

    public TesterAppService(Client client, TesterAppConfiguration configuration) {
        this.client = client;
        this.configuration = configuration;
    }

    @Override
    public void start() throws Exception {
        int count = 0;
        final ObjectMapper mapper = new ObjectMapper();

        alertSubscriptionGroups = loadAllAlertSubscriptionGroups();
        algorithmDefinitions = loadAllAlertAlgoDefinitions();
        alertTypes = loadAllAlertTypes();

        LOGGER.info("Fetched Subscription Groups: " + alertSubscriptionGroups);
        LOGGER.info("Fetched algorithm definitions: " + algorithmDefinitions);
        LOGGER.info("Fetched alert types: " + alertTypes);




        while(count < 1)
        {
            final byte[] outlier_message = Files.toByteArray(new File("/Users/ambabu/Documents/" +
                    "PersonalDocuments/code-samples/DropWizardSample/TesterApp/src/main/resources/outlier_message.json"));
            final OutlierMessage outlier = mapper.readValue(outlier_message, OutlierMessage.class);
            AlertInstance alertInstance = parseAlertInstance(outlier);
            persistAlertInstance(alertInstance);
            /*Map<String, Object> val = mapper.convertValue(out.getModelDefinition().get("modelDefinitionTags"), Map.class);*/
            count ++;
        }
    }

    @Override
    public void stop() throws Exception {
        client.close();
    }

    private void persistAlertInstance(AlertInstance instance)  {
        final String sourceURI = configuration.getAlertProcessorServiceURI() + "/api/alerts/create";
        final WebTarget webTarget = client.target(sourceURI);
        final Invocation.Builder requestBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        try {
            final Response response = requestBuilder.post(Entity.entity(instance, MediaType.APPLICATION_JSON));

            if (response.getStatus() != HttpStatus.SC_CREATED) {
                throw new RuntimeException("Failed to persist alert instance.\nStatus: " + response.getStatus());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist alert instance due to exception...", e);
        }
    }

    private AlertInstance parseAlertInstance(OutlierMessage outlierMessage) {
        final AlertInstance instance = new AlertInstance();
        final Map<String, Object> keys = new HashMap<>();

        keys.put("queue", mapper.convertValue(outlierMessage.getModelDefinition().get("modelDefinitionTags"), Map.class)
                .get("queue").toString());

        keys.put("algoDefinition", mapper.convertValue(outlierMessage.getModelDefinition().get("algoDefinition"), Map.class));

        keys.put("metricName", mapper.convertValue(outlierMessage.getModelDefinition().get("modelDefinitionTags"), Map.class)
                .get("metricName").toString());

        keys.put("keys", replaceDot(outlierMessage.getAuroraMessage().getKeys()));
        keys.put("alertTimeStamp", outlierMessage.getAlertTimestamp());
        final String hashId = generateMD5Hash(foldKeysToSingleKey(keys));
        final double alertValue = (double) outlierMessage.getAuroraMessage().getDataPoint().get("value");
        final Timestamp createdOn =  new Timestamp((long) outlierMessage.getAlertTimestamp());
        final String templateName = outlierMessage.getAuroraMessage().getKeys().get("templateName");
        final String anomalyTemplateAlias = outlierMessage.getModelDefinition().get("alias").toString();
        final AlertType alertType = getAlertType(outlierMessage.getAlertType());
        final AlgorithmDefinition algorithmDefinition = getAlgoDefinition(anomalyTemplateAlias);
        final AlertSubscriptionGroup alertSubscriptionGroup = getAlertSubscriptionGroup(anomalyTemplateAlias);
        final Set<ModelStateParameter> modelStateParams = getAlertModelState(outlierMessage.getModelState(), instance);
        final Set<AlertKeyValue> alertKeyValues = getAlertKeyValues(outlierMessage.getAuroraMessage().getKeys(), instance);

        instance.setAlertHashId(hashId);
        instance.setAlertValue(alertValue);
        instance.setCreatedOn(createdOn);
        instance.setTemplateName(templateName);
        instance.setAliasName(anomalyTemplateAlias);
        instance.setAlertType(alertType);
        instance.setAlgorithmDefinition(algorithmDefinition);
        instance.setAlertSubscriptionGroup(alertSubscriptionGroup);
        instance.setModelStateParams(modelStateParams);
        instance.setAlertKeyValues(alertKeyValues);

        return instance;
    }

    private String foldKeysToSingleKey(Map<String, Object> keys) {
        return keys.entrySet().stream().map(kv -> {
            if (Map.class.isAssignableFrom(kv.getValue().getClass())) {
                return kv.getKey() + "={" + foldKeysToSingleKey((Map<String, Object>) kv.getValue()) + "}";
            }
            return kv.getKey() + "=" + kv.getValue();
        })
                .sorted()
                .collect(Collectors.joining(":"));
    }

    private String generateMD5Hash(String data) {
        return Hex.encodeHexString(DigestUtils.getMd5Digest().digest(data.getBytes()));
    }

    /**
     * setting aurora keys (trend template keys) and their respective values in alert instance.
     *
     * @param auroraKeys
     * @param instance
     * @return
     */
    private Set<AlertKeyValue> getAlertKeyValues(final Map<String, String> auroraKeys, final AlertInstance instance)
    {
        final Set<AlertKeyValue> keyValues = new HashSet<>();

        for (final String key: auroraKeys.keySet()) {
            final AlertKeyValue keyValue = new AlertKeyValue();
            keyValue.setAlertInstance(instance);
            keyValue.setKey(key);
            keyValue.setKey(auroraKeys.get(key));

            keyValues.add(keyValue);
        }

        return keyValues;
    }

    /**
     * Setting current alert model state in alert instance.
     *
     * @param modelStateParams
     * @param instance
     * @return
     */
    private Set<ModelStateParameter> getAlertModelState(final Map<String, String> modelStateParams,
                                                        final AlertInstance instance) {
        final Set<ModelStateParameter> modelStateParameters = new HashSet<>();
        for (final String key: modelStateParams.keySet()) {
            final ModelStateParameter parameter = new ModelStateParameter();
            parameter.setKey(key);
            parameter.setValue(modelStateParams.get(key));
            parameter.setAlertInstance(instance);
            modelStateParameters.add(parameter);
        }

        return modelStateParameters;
    }



    /**
     * lookup alert subscription group for an alert instance.
     *
     * @param anomalyTemplateAlias
     * @return
     */


    private AlertSubscriptionGroup getAlertSubscriptionGroup(final String anomalyTemplateAlias) {
        for (final AlertSubscriptionGroup alertSubscriptionGroup: alertSubscriptionGroups) {
            if (alertSubscriptionGroup.getAnomalyTemplateAlias().equalsIgnoreCase(anomalyTemplateAlias))
            {
                return alertSubscriptionGroup;
            }
        }
        return null;
    }
    /**
     * lookup algorithm definition for an alert instance.
     *
     * @param templateAlias
     * @return
     */
    private AlgorithmDefinition getAlgoDefinition(final String templateAlias) {
        for (final AlgorithmDefinition algorithmDefinition: algorithmDefinitions) {
            if (algorithmDefinition.getAliasName().equalsIgnoreCase(templateAlias))
            {
                return algorithmDefinition;
            }
        }
        return null;
    }

    /**
     * lookup alert type dimension for an alert instance.
     *
     * @param alertType
     * @return
     */
    private AlertType getAlertType(final String alertType){
        for (final AlertType type: alertTypes) {
            if (type.getAlertTypeName().equalsIgnoreCase(alertType))
            {
                return type;
            }
        }
        return null;
    }

    public static  Map<String, String> replaceDot(Map<String, String> keyMap) {
        final Map<String, String> transformedKey = new HashMap<>();
        for (final Map.Entry<String, String> keyEntry : keyMap.entrySet()) {
            transformedKey.put(keyEntry.getKey().replace(".", "##"), keyEntry.getValue());
        }
        return transformedKey;
    }


    private List<AlertSubscriptionGroup> loadAllAlertSubscriptionGroups() {
        // TODO
        /**
         * call alert-processor service api to fetch all alert type definitions.
         */
        final String sourceURI = configuration.getAlertProcessorServiceURI() + "/api/alert/subscription-groups";
        final WebTarget webTarget = client.target(sourceURI);
        final Invocation.Builder requestBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        try{
            final Response response = requestBuilder.get();
            return response.readEntity(new GenericType<List<AlertSubscriptionGroup>>(){});
        } catch (Exception e)
        {
            throw new RuntimeException("Unable to fetch alert subscription groups due to exception...", e);
        }
    }

    private List<AlgorithmDefinition> loadAllAlertAlgoDefinitions() {
        // TODO
        /**
         * call alert-processor service api to fetch all alert type definitions.
         */
        final String sourceURI = configuration.getAlertProcessorServiceURI() + "/api/alert/algorithm-definitions";
        final WebTarget webTarget = client.target(sourceURI);
        final Invocation.Builder requestBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        try{
            final Response response = requestBuilder.get();
            return response.readEntity(new GenericType<List<AlgorithmDefinition>>(){});
        } catch (Exception e)
        {
            throw new RuntimeException("Unable to fetch algorithm definitions due to exception...", e);
        }
    }

    private List<AlertType> loadAllAlertTypes() {
        // TODO
        /**
         * call alert-processor service api to fetch all alert type definitions.
         */
        final String sourceURI = configuration.getAlertProcessorServiceURI() + "/api/alert-types";
        final WebTarget webTarget = client.target(sourceURI);
        final Invocation.Builder requestBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        try{
            final Response response = requestBuilder.get();
            return response.readEntity(new GenericType<List<AlertType>>(){});
        } catch (Exception e)
        {
            throw new RuntimeException("Unable to fetch alert types due to exception", e);
        }
    }

    public static void main(String[] args) throws IOException {
        /*final ObjectMapper mapper = new ObjectMapper();
        final byte[] outlier_msg = IOUtils.toByteArray(
                new FileInputStream(new File("/Users/ambabu/Documents/" +
                        "Office/projects/doppler/streamz/src/main/resources/outlier_message.json")));

        final OutlierMessage msg = mapper.readValue(outlier_msg, OutlierMessage.class);

        System.out.println(msg);*/
    }
}
