package cc.aabss.novpn.vpn;

public class IntervalNode {
    public final Interval interval;
    public int max;
    public IntervalNode left, right;

    public IntervalNode(Interval interval) {
        this.interval = interval;
        this.max = interval.end();
        this.left = null;
        this.right = null;
    }
}
