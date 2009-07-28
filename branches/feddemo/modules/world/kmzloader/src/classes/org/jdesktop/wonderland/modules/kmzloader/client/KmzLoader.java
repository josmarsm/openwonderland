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
package org.jdesktop.wonderland.modules.kmzloader.client;

import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.util.resource.ResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.jmex.model.collada.ColladaImporter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.xml.bind.JAXBElement;
import org.jdesktop.wonderland.client.jme.artimport.ImportedModel;
import org.jdesktop.wonderland.client.jme.artimport.ModelLoader;
import org.jdesktop.wonderland.client.protocols.wlzip.WlzipManager;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState.Origin;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState.Rotation;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState.Scale;
import org.jdesktop.wonderland.modules.jmecolladaloader.common.cell.state.JmeColladaCellServerState;
//import org.jdesktop.wonderland.modules.kmzloader.client.kml_22.AbstractFeatureType;
//import org.jdesktop.wonderland.modules.kmzloader.client.kml_22.AbstractGeometryType;
//import org.jdesktop.wonderland.modules.kmzloader.client.kml_21.FeatureType;
//import org.jdesktop.wonderland.modules.kmzloader.client.kml_21.FolderType;
//import org.jdesktop.wonderland.modules.kmzloader.client.kml_21.GeometryType;
//import org.jdesktop.wonderland.modules.kmzloader.client.kml_21.ModelType;
//import org.jdesktop.wonderland.modules.kmzloader.client.kml_21.PlacemarkType;

/**
 *
 * Loader for SketchUp .kmz files
 * 
 * @author paulby
 */
class KmzLoader implements ModelLoader {

    private static final Logger logger = Logger.getLogger(KmzLoader.class.getName());
        
    private HashMap<URL, ZipEntry> textureFiles = new HashMap();
    
    // The original file the user loaded
    private File origFile;
    
    private ArrayList<String> modelFiles = new ArrayList();

    private Node modelNode = null;
    
    /**
     * Load a SketchUP KMZ file and return the graph root
     * @param file
     * @return
     */
    public Node importModel(File file) throws IOException {
        modelNode = null;
        origFile = file;
        
        try {
            ZipFile zipFile = new ZipFile(file);
            ZipEntry docKmlEntry = zipFile.getEntry("doc.kml");
//            JAXBContext jc = JAXBContext.newInstance("org.jdesktop.wonderland.modules.kmzloader.client.kml_21",
//                                                     getClass().getClassLoader());
//            Unmarshaller u = jc.createUnmarshaller();
//            JAXBElement docKml = (JAXBElement) u.unmarshal(zipFile.getInputStream(docKmlEntry));
//
//            KmlType kml = (KmlType) docKml.getValue();
//
//            ArrayList<ModelType> models=new ArrayList();
//            FeatureType feature = kml.getFeature().getValue();
//            // For the 2.2 version of KML
//            //AbstractFeatureType feature = kml.getAbstractFeatureGroup().getValue();
//            if (feature instanceof FolderType) {
//                findModels(models, (FolderType)feature);
//            }
//
//            if (models.size()==0) {
//                logger.severe("No models found in KMZ File");
//                return null;
//            }

            KmlParser parser = new KmlParser();
            InputStream in = zipFile.getInputStream(docKmlEntry);
            try {
                parser.decodeKML(in);
            } catch (Exception ex) {
                Logger.getLogger(KmzLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
            List<KmlParser.KmlModel> models = parser.getModels();

            if (models.size()==1) {
                modelNode = load(zipFile, models.get(0));
            } else {
                modelNode = new Node();
                for(KmlParser.KmlModel model : models) {
                    modelNode.attachChild(load(zipFile, model));
                }
            }
            
        } catch (ZipException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new IOException("Zip Error");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw ex;
        } 
        
        return modelNode;
    }
    
    private Node load(ZipFile zipFile, KmlParser.KmlModel model) throws IOException {

        String filename = model.getHref();
        String zipHost = WlzipManager.getWlzipManager().addZip(zipFile);
        ZipResourceLocator zipResource = new ZipResourceLocator(zipHost, zipFile);
        ResourceLocatorTool.addResourceLocator(
                ResourceLocatorTool.TYPE_TEXTURE,
                zipResource);

        logger.info("Loading MODEL " + filename);
        modelFiles.add(filename);
        
        ZipEntry modelEntry = zipFile.getEntry(filename);
        BufferedInputStream in = new BufferedInputStream(zipFile.getInputStream(modelEntry));

        ColladaImporter.load(in, filename);
        modelNode = ColladaImporter.getModel();

        // Adjust the scene transform to match the scale and axis specified in
        // the collada file
        float unitMeter = ColladaImporter.getInstance().getUnitMeter();
        modelNode.setLocalScale(unitMeter);

        String upAxis = ColladaImporter.getInstance().getUpAxis();
        if (upAxis.equals("Z_UP")) {
            modelNode.setLocalRotation(new Quaternion(new float[] {-(float)Math.PI/2, 0f, 0f}));
        } else if (upAxis.equals("X_UP")) {
            modelNode.setLocalRotation(new Quaternion(new float[] {0f, 0f, (float)Math.PI/2}));
        } // Y_UP is the Wonderland default

        ColladaImporter.cleanUp();
        
        ResourceLocatorTool.removeResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, zipResource);
        WlzipManager.getWlzipManager().removeZip(zipHost, zipFile);

        return modelNode;
    }

    /**
     * Search kmz folders adding any ModelTypes found to the models list
     * @param models
     * @param folder
     */
//    private void findModels(ArrayList<ModelType> models, FolderType folder) {
//        List<JAXBElement<? extends FeatureType>> features = folder.getFeature();
//        for(JAXBElement<? extends FeatureType> featureJAXB : features) {
//            FeatureType feature = featureJAXB.getValue();
//
//            if (feature instanceof FolderType) {
//                findModels(models, (FolderType)feature);
//            } else if (feature instanceof PlacemarkType) {
//                if (((PlacemarkType)feature).getGeometry()!=null) {
//                    GeometryType geometryType = ((PlacemarkType)feature).getGeometry().getValue();
//                    if (geometryType instanceof ModelType) {
//                        models.add((ModelType)geometryType);
//                    } else {
//                        logger.info("Unsupported GeometryType "+geometryType);
//                    }
//                }
//            } else
//                logger.info("Skipping feature "+feature);
//        }
//    }

    // For the 2.2 version of KML
//    private void findModels22(ArrayList<ModelType> models, FolderType folder) {
//        List<JAXBElement<? extends AbstractFeatureType>> features = folder.getAbstractFeatureGroup();
//        for(JAXBElement<? extends AbstractFeatureType> featureJAXB : features) {
//            AbstractFeatureType feature = featureJAXB.getValue();
//
//            if (feature instanceof FolderType) {
//                findModels(models, (FolderType)feature);
//            } else if (feature instanceof PlacemarkType) {
//                if (((PlacemarkType)feature).getAbstractGeometryGroup()!=null) {
//                    AbstractGeometryType geometryType = ((PlacemarkType)feature).getAbstractGeometryGroup().getValue();
//                    if (geometryType instanceof ModelType) {
//                        models.add((ModelType)geometryType);
//                    } else {
//                        logger.info("Unsupported GeometryType "+geometryType);
//                    }
//                }
//            } else
//                logger.info("Skipping feature "+feature);
//        }
//    }
    
    public ModelDeploymentInfo deployToModule(File moduleRootDir, ImportedModel model) throws IOException {
        try {
            String modelName = origFile.getName();
            ZipFile zipFile = new ZipFile(origFile);
            
            // TODO replace getName with getModuleName(moduleRootDir)
            String moduleName = moduleRootDir.getName();

            String targetDirName = moduleRootDir.getAbsolutePath()+File.separator+"art"+ File.separator + modelName;
            File targetDir = new File(targetDirName);
            targetDir.mkdir();

            deployTextures(zipFile, targetDir);
            deployModels(zipFile, targetDir);

            if (modelFiles.size() > 1) {
                logger.warning("Multiple models not supported during deploy");
            }

            // XXX There should not be a direct reference to another module
            // from here.
            JmeColladaCellServerState setup = new JmeColladaCellServerState();
            setup.setModel("wla://"+moduleName+"/"+modelName+"/"+modelFiles.get(0));
            setup.setGeometryRotation(new Rotation(modelNode.getLocalRotation()));
            setup.setGeometryScale(new Scale(modelNode.getLocalScale()));

            Vector3f offset = model.getRootBG().getLocalTranslation();
            PositionComponentServerState position = new PositionComponentServerState();
            Vector3f boundsCenter = model.getRootBG().getWorldBound().getCenter();

            offset.subtractLocal(boundsCenter);

            setup.setGeometryTranslation(new Origin(offset));

//            System.err.println("BOUNDS CENTER "+boundsCenter);
//            System.err.println("OFfset "+offset);
//            System.err.println("Cell origin "+boundsCenter);
            position.setOrigin(new Origin(boundsCenter));

            // The cell bounds already have the rotation and scale applied, so these
            // values must not go in the Cell transform. Instead they go in the
            // JME cell setup so that the model is correctly oriented and thus
            // matches the bounds in the cell.
            
            // Center the worldBounds on the cell (ie 0,0,0)
            BoundingVolume worldBounds = modelNode.getWorldBound();
            worldBounds.setCenter(new Vector3f(0,0,0));
            position.setBounds(worldBounds);

//            System.err.println("Deploying with bounds "+worldBounds);

            setup.addComponentServerState(position);

            ModelDeploymentInfo deploymentInfo = new ModelDeploymentInfo();
            deploymentInfo.setCellSetup(setup);
            return deploymentInfo;
        } catch (ZipException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new IOException("Zip error");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw ex;
        }        
    }
    
    /**
     * KMZ files keep all the models in the /models directory, copy all the
     * models into the module
     * @param moduleArtRootDir
     */
    private void deployModels(ZipFile zipFile, File targetDir) {
        
        // TODO update collada files with module relative texture paths
        
        try {
            String targetDirName = targetDir.getAbsolutePath();
            
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".dae")) {
                    File target = new File(targetDirName+File.separator+entry.getName());
                    target.getParentFile().mkdirs();
                    target.createNewFile();
                    
                    copyAsset(zipFile, entry, target);
                }
            }
            
            
        } catch (ZipException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * Deploys the textures into the art directory, placing them in a directory
     * with the name of the original model.
     * @param moduleArtRootDir
     */
    private void deployTextures(ZipFile zipFile, File targetDir) {
        try {
            // TODO generate checksums to check for image duplication
            String targetDirName = targetDir.getAbsolutePath();

            for (Map.Entry<URL, ZipEntry> t : textureFiles.entrySet()) {
                File target = new File(targetDirName + File.separator + t.getKey().getPath());
                target.getParentFile().mkdirs();
                target.createNewFile();
//                logger.fine("Texture file " + target.getAbsolutePath());
                copyAsset(zipFile, t.getValue(), target);
            }
        } catch (ZipException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Copy the asset from the zipEntry to the target file
     * @param zipFile the zipFile that contains the zipEntry
     * @param zipEntry entry to copy from
     * @param target file to copy to
     */
    private void copyAsset(ZipFile zipFile, ZipEntry zipEntry, File target) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(zipFile.getInputStream(zipEntry));
            out = new BufferedOutputStream(new FileOutputStream(target));
            
            org.jdesktop.wonderland.common.FileUtils.copyFile(in, out);
            
            
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (in!=null)
                    in.close();
                if (out!=null)
                    out.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    class ZipResourceLocator implements ResourceLocator {

        private String zipHost;
        private ZipFile zipFile;
        
        public ZipResourceLocator(String zipHost, ZipFile zipFile) {
            this.zipHost = zipHost;
            this.zipFile = zipFile;
        }
        
        public URL locateResource(String filename) {
            // Texture paths seem to be relative to the model directory....
            if (filename.startsWith("../")) {
                filename = filename.substring(3);
            }
            if (filename.startsWith("/")) {
                filename = filename.substring(1);
            }
            
            ZipEntry entry = zipFile.getEntry(filename);
            if (entry==null) {
                logger.severe("Unable to locate texture "+filename);
                return null;
            }
            
            try {
                URL url = new URL("wlzip", zipHost, "/"+filename);
                textureFiles.put(url, entry);
                return url;
            } catch (MalformedURLException ex) {
                Logger.getLogger(KmzLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
    

}