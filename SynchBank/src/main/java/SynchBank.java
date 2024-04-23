import java.util.Random;

class Bank {
    private final double[] accounts;

    public Bank(int n, double initBalance) {
        accounts = new double[n];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = initBalance;
        }
    }

    public synchronized int size() {
        return accounts.length;
    }

    public synchronized double getTotalBalance() {
        double total = 0;
        for (double balance : accounts) {
            total += balance;
        }
        return total;
    }

    public synchronized void transfer(int from, int to, double amount) throws InterruptedException {
        while (accounts[from] < amount) {
            System.out.println(Thread.currentThread().getName() + " đợi đủ tiền");
            wait();
            System.out.println(Thread.currentThread().getName() + " tiếp tục giao dịch");
        }

        accounts[from] -= amount;
        accounts[to] += amount;

        System.out.println("Chuyển " + amount + " từ account " + from + " sang account " + to);
        System.out.println("Tổng tiền của tất cả các account: " + getTotalBalance());

        notifyAll();
    }
}

class TransferMoney implements Runnable {
    private Bank bank;
    private int fromAcc;
    private double maxAmount;
    private static final int DELAY = 1000;

    public TransferMoney(Bank bank, int fromAcc, double maxAmount) {
        this.bank = bank;
        this.fromAcc = fromAcc;
        this.maxAmount = maxAmount;
    }

    @Override
    public void run() {
        Random rd = new Random();
        try {
            while (true) {
                int toAcc = rd.nextInt(bank.size());
                double amount = rd.nextDouble() * maxAmount;
                bank.transfer(fromAcc, toAcc, amount);
                Thread.sleep(rd.nextInt(DELAY));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class SynchBank {
    public static void main(String[] args) {
        Bank bank = new Bank(100, 1000);
        int size = bank.size();
        for (int i = 0; i < size; i++) {
            TransferMoney transferMoney = new TransferMoney(bank, i, 1000);
            Thread thread = new Thread(transferMoney);
            thread.start();
        }
    }
}
