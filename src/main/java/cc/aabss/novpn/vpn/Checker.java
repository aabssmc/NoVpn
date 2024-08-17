package cc.aabss.novpn.vpn;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Checker {
    public static String fetchCidrRanges() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://raw.githubusercontent.com/josephrocca/is-vpn/main/vpn-or-datacenter-ipv4-ranges.txt"))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public static int ipToBinary(String ip) {
        String[] octets = ip.split("\\.");
        int binaryIp = 0;
        for (String octet : octets) {
            binaryIp = (binaryIp << 8) + Integer.parseInt(octet);
        }
        return binaryIp;
    }

    public static Interval ipv4CidrToRange(String cidr) {
        String[] parts = cidr.split("/");
        String baseIp = parts[0];
        int subnetMask = Integer.parseInt(parts[1]);

        int ipBinary = ipToBinary(baseIp);
        int rangeEnd = ipBinary | ((1 << (32 - subnetMask)) - 1);

        return new Interval(ipBinary, rangeEnd);
    }
}
