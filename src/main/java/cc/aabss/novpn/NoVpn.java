package cc.aabss.novpn;

import cc.aabss.novpn.vpn.Interval;
import cc.aabss.novpn.vpn.IntervalTree;

import java.util.concurrent.CompletableFuture;

import static cc.aabss.novpn.vpn.Checker.*;

public interface NoVpn {

    IntervalTree itree = new IntervalTree();

    default boolean isVpn(String ip) {
        int ipBinary = ipToBinary(ip);
        return itree.query(ipBinary);
    }

    default void updateList() {
        CompletableFuture.runAsync(() -> {
            try {
                IntervalTree newTree = new IntervalTree();
                String ipv4CidrRanges = fetchCidrRanges();
                if (ipv4CidrRanges != null) {
                    String[] ranges = ipv4CidrRanges.split("\n");
                    for (String cidr : ranges) {
                        Interval range = ipv4CidrToRange(cidr);
                        newTree.insert(range);
                    }
                }
                itree.root = newTree.root;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
