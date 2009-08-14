/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath"
 * exception as provided by Sun in the License file that accompanied
 * this code.
 */
package org.jdesktop.wonderland.modules.artimport.client.jme;

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import org.jdesktop.mtgame.NewFrameCondition;
import org.jdesktop.mtgame.ProcessorArmingCollection;
import org.jdesktop.mtgame.ProcessorComponent;
import org.jdesktop.mtgame.WorldManager;

/**
 *
 * @author paulby
 */
public class TransformProcessorComponent extends ProcessorComponent {


        private Matrix3f rotation;
        private Vector3f translation;
        private Vector3f scale = new Vector3f(1,1,1);
        private Node node;
        private WorldManager worldManager;
        private boolean updatePending = false;
        
        public TransformProcessorComponent(WorldManager worldManager, Node node) {
            this.node = node;
            this.worldManager = worldManager;
        }
        
        @Override
        public void compute(ProcessorArmingCollection conditions) {
            // Nothing to do
        }

        @Override
        public void commit(ProcessorArmingCollection conditions) {
            synchronized(this) {
                if (updatePending) {
                    node.setLocalRotation(rotation);
                    node.setLocalTranslation(translation);
                    node.setLocalScale(scale);
                    updatePending = false;
                }

            }
            worldManager.addToUpdateList(node);
        }

        @Override
        public void initialize() {
            setArmingCondition(new NewFrameCondition(this));
        }

        public void setTransform(Matrix3f rotation, Vector3f translation) {
            synchronized(this) {
                this.rotation = rotation;
                this.translation = translation;
                updatePending = true;
            }
        }

        public void setTransform(Matrix3f rotation, Vector3f translation, Vector3f scale) {
            synchronized(this) {
                this.rotation = rotation;
                this.translation = translation;
                this.scale = scale;
                updatePending = true;
            }
        }
    
}