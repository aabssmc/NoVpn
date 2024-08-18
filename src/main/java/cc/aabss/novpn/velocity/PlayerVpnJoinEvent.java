package cc.aabss.novpn.velocity;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.Player;

import java.net.InetSocketAddress;

public class PlayerVpnJoinEvent implements ResultedEvent<ResultedEvent.GenericResult> {
    private GenericResult result;
    private final Player player;
    private final InetSocketAddress ip;

    public PlayerVpnJoinEvent(Player player, InetSocketAddress ip) {
        this.player = player;
        this.ip = ip;
        this.result = GenericResult.allowed();
    }

    public Player getVote() {
        return player;
    }

    public InetSocketAddress getIp() {
        return ip;
    }

    @Override
    public GenericResult getResult() {
        return this.result;
    }

    @Override
    public void setResult(GenericResult result) {
        this.result = result;
    }
}
