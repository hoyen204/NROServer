package com.arriety.card;

import com.girlkun.models.player.Player;
import com.girlkun.network.io.Message;
import java.util.ArrayList;
import java.util.List;

/**
 * Optimized RadarService by Dev Duy Peo
 */
public class RadarService {
    private static RadarService instance;

    public final List<RadarCard> RADAR_TEMPLATE = new ArrayList<>();

    private RadarService() {}

    public static synchronized RadarService gI() {
        if (instance == null) {
            instance = new RadarService();
        }
        return instance;
    }

    public void sendRadar(Player pl, List<Card> cards) {
        Message m = null;
        try {
            m = new Message(127);
            m.writer().writeByte(0);
            m.writer().writeShort(RADAR_TEMPLATE.size());

            for (RadarCard radar : RADAR_TEMPLATE) {
                Card card = findCard(cards, radar.id);
                m.writer().writeShort(radar.id);
                m.writer().writeShort(radar.iconId);
                m.writer().writeByte(radar.rank);
                m.writer().writeByte(card.amount);
                m.writer().writeByte(card.maxAmount);
                m.writer().writeByte(radar.type);

                if (radar.type == 0) {
                    m.writer().writeShort(radar.template);
                } else {
                    m.writer().writeShort(radar.head);
                    m.writer().writeShort(radar.body);
                    m.writer().writeShort(radar.leg);
                    m.writer().writeShort(radar.bag);
                }

                m.writer().writeUTF(radar.name);
                m.writer().writeUTF(radar.info);
                m.writer().writeByte(card.level);
                m.writer().writeByte(card.used);

                m.writer().writeByte(radar.options.size());
                for (OptionCard option : radar.options) {
                    m.writer().writeByte(option.id);
                    m.writer().writeShort(option.param);
                    m.writer().writeByte(option.active);
                }
            }

            m.writer().flush();
            pl.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace(); // Log error instead of silent fail
        } finally {
            if (m != null) {
                m.cleanup(); // Manually cleanup
            }
        }
    }

    private Card findCard(List<Card> cards, int id) {
        for (Card c : cards) {
            if (c.id == id) {
                return c;
            }
        }
        return new Card((byte)0, new ArrayList<>()); // Default empty card
    }

    public void Radar1(Player pl, short id, int use) {
        sendRadarUpdate(pl, (byte) 1, id, (byte) use);
    }

    public void RadarSetLevel(Player pl, int id, int level) {
        sendRadarUpdate(pl, (byte) 2, id, (byte) level);
    }

    public void RadarSetAmount(Player pl, int id, int amount, int maxAmount) {
        Message message = null;
        try {
            message = new Message(127);
            message.writer().writeByte(3);
            message.writer().writeShort(id);
            message.writer().writeByte(amount);
            message.writer().writeByte(maxAmount);
            message.writer().flush();
            pl.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (message != null) {
                message.cleanup();
            }
        }
    }

    private void sendRadarUpdate(Player pl, byte type, int id, byte value) {
        Message message = null;
        try {
            message = new Message(127);
            message.writer().writeByte(type);
            message.writer().writeShort(id);
            message.writer().writeByte(value);
            message.writer().flush();
            pl.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (message != null) {
                message.cleanup();
            }
        }
    }
}