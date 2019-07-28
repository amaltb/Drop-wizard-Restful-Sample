import java.sql.Timestamp;

public class Test {

    static void throwEx()
    {
        throw new RuntimeException("runtime");
    }

    public static void main(String[] args) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        Timestamp es = new Timestamp(System.currentTimeMillis());

        System.out.println(es);

        System.out.println(ts.getTime() - es.getTime());
        System.out.println(ts.equals(es));


        System.out.println(new Timestamp(ts.getTime() - 24 * 60 * 60 * 1000));

        try {
            throwEx();
        } catch (Exception e)
        {
            if(e instanceof RuntimeException)
            {
                System.out.println("It was a runtime error");
            }
        }
    }
}
