/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2008, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * $Revision$
 * $Date$
 * $State$
 */
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.jme.cellrenderer.*;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import imi.character.CharacterMotionListener;
import imi.character.ninja.Ninja;
import imi.character.ninja.NinjaAvatar;
import imi.character.statemachine.GameContextListener;
import imi.scene.JScene;
import imi.scene.PMatrix;
import imi.scene.processors.JSceneEventProcessor;
import imi.utils.input.NinjaControlScheme;
import java.util.ArrayList;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.MovableComponent;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellTransform;
import imi.character.ninja.NinjaAvatar;
import imi.character.Character;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.login.LoginManager;

/**
 * Renderer for Avatar, looks strangely like a teapot at the moment...
 * 
 * @author paulby
 */
@ExperimentalAPI
public class AvatarImiJME extends BasicRenderer {

    public AvatarImiJME(Cell cell) {
        super(cell);
        assert(cell!=null);
    }
    
    @Override
    protected Entity createEntity() {
        Entity ret = createDemoEntities(ClientContextJME.getWorldManager());

        RenderComponent rc = (RenderComponent) ret.getComponent(RenderComponent.class);
        if (rc!=null)
            addDefaultComponents(ret, rc.getSceneRoot());
        else
            logger.warning("NO RenderComponent for Avatar");

        return ret;
    }

    @Override
    protected void addRenderState(Node node) {
        // Nothing to do
    }
    
    protected Entity createDemoEntities(WorldManager wm) {
        PMatrix origin = new PMatrix();
        CellTransform transform = cell.getLocalTransform();
        origin.setTranslation(transform.getTranslation(null));
        origin.setRotation(transform.getRotation(null));
        
//        Ninja avatar = new Ninja("Shadow Blade", origin, /*"assets/configurations/ninjaDude.xml",*/ 0.22f, wm);
        NinjaAvatar avatar;
        avatar = new AvatarCharacter("Avatar", wm);
        NinjaControlScheme control = (NinjaControlScheme)((JSceneEventProcessor)wm.getUserData(JSceneEventProcessor.class)).setDefault(new NinjaControlScheme(avatar));
        avatar.selectForInput();
        control.getNinjaTeam().add(avatar);

//        JScene jscene = avatar.getJScene();
//        jscene.renderToggle();      // both renderers
//        jscene.renderToggle();      // jme renderer only
//        jscene.setRenderPRendererMesh(true);  // Force pRenderer to be instantiated
//        jscene.toggleRenderPRendererMesh();   // turn off mesh
//        jscene.toggleRenderBoundingVolume();  // turn off bounds

        // Listen for avatar movement and update the cell
        avatar.getController().addCharacterMotionListener(new CharacterMotionListener() {

            public void transformUpdate(Vector3f translation, PMatrix rotation) {
                cell.getComponent(MovableComponent.class).localMoveRequest(new CellTransform(rotation.getRotation(), translation));
            }
        });

        // Listen for game context changes
        avatar.getContext().addGameContextListener(new GameContextListener() {

            public void trigger(boolean pressed, int trigger, Vector3f translation, Quaternion rotation) {
                System.err.println(pressed+" "+trigger+" "+translation);
            }

        });

        wm.removeEntity(avatar);
        
        return avatar;
    }

    @Override
    protected Node createSceneGraph(Entity entity) {
        // Nothing to do here
        return null;
    }

    class AvatarCharacter extends NinjaAvatar {

        public class MaleAvatarAttributes extends Character.Attributes
        {
            public MaleAvatarAttributes(String name) {
                super(name);
                setModelFile("assets/models/collada/Avatars/Male2/Male_Bind.dae");
                ArrayList<String> anims = new ArrayList<String>();
                anims.add("assets/models/collada/Avatars/MaleZip/Male_Idle.dae");
                anims.add("assets/models/collada/Avatars/MaleZip/Male_StandToSit.dae");
                anims.add("assets/models/collada/Avatars/MaleZip/Male_Wave.dae");
                anims.add("assets/models/collada/Avatars/MaleZip/Male_Walk.dae");
                anims.add("assets/models/collada/Avatars/MaleZip/Male_Sitting.dae");
                if (false)
                {
                    anims.add("assets/models/collada/Avatars/MaleZip/Male_Run.dae");
                    anims.add("assets/models/collada/Avatars/Male/Male_Bow.dae");
                    anims.add("assets/models/collada/Avatars/Male/Male_Cheer.dae");
                    anims.add("assets/models/collada/Avatars/Male/Male_Clap.dae");
                    anims.add("assets/models/collada/Avatars/Male/Male_Follow.dae");
                    anims.add("assets/models/collada/Avatars/Male/Male_Jump.dae");
                    anims.add("assets/models/collada/Avatars/Male/Male_Laugh.dae");
                }
                setAnimations(anims.toArray(new String[anims.size()]));

                WonderlandSession session = cell.getCellCache().getSession();
                LoginManager manager = LoginManager.find(session);
                String serverHostAndPort = manager.getServerNameAndPort();

                setBaseURL("wla://avatarbase@"+serverHostAndPort+"/");

//                setUseSimpleSphereModel(true);
            }

        }

        public AvatarCharacter(String name, WorldManager wm) {
            super(name,wm);
        }

        @Override
        protected Attributes createAttributes(String name)
        {
            return new MaleAvatarAttributes(name);
        }
    }

}
