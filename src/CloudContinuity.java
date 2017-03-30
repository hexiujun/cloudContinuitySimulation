/**
 * Created by j on 2017/3/6.
 */


public class CloudContinuity {
    int T;                      // 断电周期为[0, T]

    // 能耗参数
    double alpha0;              // 能耗因子
    double beta0;              // 私有云服务器空闲时能耗
    double e;                   // 单位时间内能耗预算

    // 时延参数
    double delay;                   // 用户所能容忍的最大时延

    // 成本参数
    double A;                   // 公有云租用单台服务器的成本

    // 惩罚系数
    double V;

    // 公有云、私有云参数
    double miuV;                // 私有云服务器最大处理速率
    double miuU;                // 公有云虚拟机最大处理速率

    // 隧道模型
    Tunnel tunnel;

    // 泊松分布参数
    int lambda;               // 单位时间内用户请求的平均到达量

    // 输入数据
    int[] arrivals;
    double precision;

    // 决策变量
    double[] requestRate;

    // 输出数据
    double[] cost;
    double[] energyQueue;

    /**
     * 参数初始化
     */
    public void init() {
        T = 100;
        lambda = 200;

        alpha0 = 0.25;
        beta0 = 100.0;
        e = 10000000.0;
        delay = 100000;
        A = 1;
        V = 1000;
        miuV = 100.0;
        miuU = 100.0;

        tunnel = new Tunnel();
        arrivals = RequestGenerator.generatePoisson(lambda, T);
        precision = 0.01;

        requestRate = new double[T];
        cost = new double[T];
        energyQueue = new double[T];
    }

    /**
     * 我们设计的在线调度算法
     */
    public void scheduler() {
        for (int timeSlot = 0; timeSlot < T; timeSlot++) {
            int arrival = arrivals[timeSlot];
            double backupInEnergyQueue = timeSlot > 0 ? energyQueue[timeSlot - 1] : 0;

            requestRate[timeSlot] = precision;
            cost[timeSlot] = 0.0;
            energyQueue[timeSlot] = backupInEnergyQueue - e;
            double zOpt = Double.MAX_VALUE;
            for (double alpha = precision; alpha < 1; alpha += precision) {
                for (int level = 0; level < tunnel.L; level++) {
                    double miuR = tunnel.bandwidth[level];
                    if (miuR - (1 - alpha) * arrival <= 0) continue;
                    double delayR = (1 - alpha) / (miuR - (1 - alpha) * arrival);
                    if (delayR >= delay) continue;
                    double  c1 = backupInEnergyQueue * (alpha0 + beta0 / miuV),
                            c2 = V * A * (1 - alpha) / miuU,
                            c3 = c2 * arrival;
                    double xt = arrival * alpha + (alpha + Math.sqrt(c2 * alpha / c1)) / (delay - delayR);
                    double nut = ((1 - alpha) * (arrival + 1 / (delay - delayR - alpha / (xt - arrival * alpha)))) / miuU;
                    double zt = c1 * xt + V * (A * nut + tunnel.price[level]);
                    if (zt < zOpt) {
                        zOpt = zt;
                        requestRate[timeSlot] = alpha;
                        cost[timeSlot] = A * nut + tunnel.price[level];
                        energyQueue[timeSlot] = backupInEnergyQueue - e + c1 * xt / backupInEnergyQueue;
                    }
                }
            }
            energyQueue[timeSlot] = Math.max(0.0, energyQueue[timeSlot]);
        }
    }

    /**
     * 私有云优先调度算法，作为对比实验
     */
    public void privateCloudFirstScheduler() {




    }

    public void print() {
        System.out.println("timeSlot requestRate cost energyQueue");
        for (int i = 0; i < T; i++) {
            System.out.println("" + i + "\t" + requestRate[i] + "\t" + cost[i] + "\t" + energyQueue[i]);
        }
    }

    /**
     * 主功能函数
     * @param args
     */
    public static void main(String[] args) throws Exception {
        CloudContinuity task = new CloudContinuity();
        task.init();

        task.scheduler();
        task.print();
    }

}
