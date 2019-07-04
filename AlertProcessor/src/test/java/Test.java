import java.sql.Timestamp;

public class Test {

    public static void main(String[] args) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Timestamp es = new Timestamp(System.currentTimeMillis());

        System.out.println(ts.getTime() - es.getTime());
        System.out.println(ts.equals(es));



        System.out.println(new Timestamp(ts.getTime() - 24 * 60 * 60 * 1000));
    }
}
