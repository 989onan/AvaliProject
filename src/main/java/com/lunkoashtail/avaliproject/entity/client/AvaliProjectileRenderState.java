package com.lunkoashtail.avaliproject.entity.client;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;

import java.util.Map;

public class AvaliProjectileRenderState extends EntityRenderState{
    public float yRotO;
    public float xRotO;
    public float getXRot0;
    public float getYRot0;

    //we don't need anything here for this.
    //classes like these are made to communicate data from the entity stage to the rendering stage.
    //like position of the entity and so forth. - @989onan
}
