package neuron;

public class Neuron {
    private double state;
    private double threshold;

    public double getState() {
        return state;
    }

    public void setState(double state) {
        this.state = state;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }
}
