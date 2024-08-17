package cc.aabss.novpn.vpn;

public class IntervalTree {
    private IntervalNode root;

    public void insert(Interval interval) {
        if (root == null) {
            root = new IntervalNode(interval);
            return;
        }

        IntervalNode node = root;
        while (true) {
            node.max = Math.max(node.max, interval.end());

            if (interval.start() < node.interval.start()) {
                if (node.left == null) {
                    node.left = new IntervalNode(interval);
                    break;
                }
                node = node.left;
            } else {
                if (node.right == null) {
                    node.right = new IntervalNode(interval);
                    break;
                }
                node = node.right;
            }
        }
    }

    public boolean query(int point) {
        IntervalNode node = root;
        while (node != null) {
            if (point >= node.interval.start() && point <= node.interval.end()) {
                return true;
            }
            if (node.left != null && point <= node.left.max) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        return false;
    }
}