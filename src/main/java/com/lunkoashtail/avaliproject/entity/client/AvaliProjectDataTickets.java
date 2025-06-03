package com.lunkoashtail.avaliproject.entity.client;

import software.bernie.geckolib.constant.dataticket.DataTicket;

public class AvaliProjectDataTickets {
    public static DataTicket<String> texture = DataTicket.create("texture", String.class);
    public static DataTicket<String> partialTick = DataTicket.create("partialTick", String.class);
    public static DataTicket<Boolean> IsBaby = DataTicket.create("is_baby", Boolean.class);

}
