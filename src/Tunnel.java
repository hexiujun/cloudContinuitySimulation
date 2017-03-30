/**
 * Created by j on 2017/3/11.
 *
 * Amazon Direct Connection隧道模型
 */
public class Tunnel {
    // 可调控的带宽级别数
    final int L = 7;

    // 不同级别的传输带宽,单位（Mbps）
    final double[] bandwidth = new double[]{50, 100, 200, 300, 400, 500, 10000};

    // 不同级别传输带宽的定价，单位(USD/hour)
    final double[] price = new double[]{0.03, 0.06, 0.12, 0.18, 0.24, 0.30, 2.25};
}
