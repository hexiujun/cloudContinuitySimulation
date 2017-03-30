/**
 * Created by j on 2017/3/8.
 */
public class RequestGenerator {
    public static int generatePoisson(int lambda) {
        int num = 0;
        double c = Math.exp(-lambda);
        double sum = 1.0;
        double pdf = c * sum;

        double threshold = Math.random();

        while (pdf < threshold) {
            num++;
            sum *= (double)lambda / num;
            pdf += c * sum;
        }

        return num;
    }

    public static int[] generatePoisson(int lambda, int capacity) {
        int[] output = new int[capacity];
        for (int i = 0; i < capacity; i++) {
            output[i] = generatePoisson(lambda);
        }
        return output;
    }
}
