package sciviewtest;

import clearcl.imagej.ClearCLIJ;
import cleargl.GLVector;
import graphics.scenery.Mesh;
import graphics.scenery.Node;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.NewImage;
import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imagej.ops.geom.geom3d.mesh.BitTypeVertexInterpolator;
import net.imagej.patcher.LegacyInjector;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.view.Views;
import org.scijava.Context;
import org.scijava.ui.UIService;
import sc.iview.SciView;
import sc.iview.SciViewService;

public class SciViewTest {


    static {
        LegacyInjector.preinit();
    }

    public static void main(String... args) {

        ImageJ ij = new ImageJ();

        System.setProperty( "scijava.log.level:sc.iview", "debug" );
        Context context = ij.context(); //new Context( ImageJService.class, SciJavaService.class, SCIFIOService.class, ThreadService.class);

        UIService ui = context.service( UIService.class );
        if( !ui.isVisible() ) ui.showUI();
        //ui.show(NewImage.createByteImage("tmp", 1,1,1, NewImage.FILL_BLACK));

        SciViewService sciViewService = context.service( SciViewService.class );
        //sciViewService.createSciView();
        //sciViewService.createSciView();
        //sciViewService.initialize();

        // I'm getting a null back here resulting in a NullPointerException later:
        SciView sciView = sciViewService.getActiveSciView();

        // If I execute alternatively this line of code, it opens a window, but my code is no longer executed...
        //SciView sciView = sciViewService.getOrCreateActiveSciView();

        sciView.getCamera().setPosition( new GLVector( 0.0f, 0.0f, 5.0f ) );
        sciView.getCamera().setTargeted( true );
        sciView.getCamera().setTarget( new GLVector( 0, 0, 0 ) );
        sciView.getCamera().setDirty( true );
        sciView.getCamera().setNeedsUpdate( true );

        // that's the image I would l
        ImagePlus imp = IJ.openImage("src/main/resources/simdata.tif");

        ClearCLIJ clij = ClearCLIJ.getInstance();
        clij.show(imp, "debug");

        RandomAccessibleInterval<UnsignedShortType> rai = clij.converter(imp).getRandomAccessibleInterval();

        if (sciView.getActiveNode() != null)  {
            // todo: remove old image

            //sciView.removeMesh(sciView.updateVolume());
        }

        sciView.init();
        Node v = sciView.addVolume( Views.iterable(rai), imp.getTitle(), new float[] { 1, 1, 1 } );
        v.setName( "Volume Render Demo" );

        OpService ops = ij.op();

        int isoLevel = 1;

        //        @SuppressWarnings("unchecked")
        Img<UnsignedShortType> cubeImg = ( Img<UnsignedShortType> ) rai;

        //Img<BitType> bitImg = ( Img<BitType> ) ops.threshold().apply( cubeImg, new UnsignedShortType( isoLevel ) );
        Img<BitType> bitImg = ( Img<BitType> ) ops.threshold().maxEntropy( cubeImg );
        ij.ui().show(bitImg);

        Mesh m = (Mesh) ops.geom().marchingCubes( bitImg, isoLevel, new BitTypeVertexInterpolator() );

        sciView.addMesh( m ).setName( "Volume Render Demo Isosurface" );


        sciView.centerOnNode( sciView.getActiveNode() );

    }

    private void whatever()
    {
        /*
        // get sciview
        var sc = sciView.getActiveSciView();


// toggle unlimited framerate, needed for screenshots due to scenerygraphics/scenery#213
        sc.setPushMode(false);

// HDR settings
        settings = sciView.getActiveSciView().getScenerySettings();
        settings.set("Renderer.HDR.Exposure", 20.0);
        settings.set("Renderer.HDR.Gamma", 1.8);


// get scene
        var scene = sc.getAllSceneNodes()[0].parent;

// careful, index might change
        var fish = scene.children[6];
        fish.renderScale = 0.05;
        fish.transferFunction = TransferFunction.flat(0.01);

        var files = new File("L:/segmented_vascular_data2/171202_vasculatureMovie/tiff/e004/regions_combined/angle000/").listFiles();
        Arrays.sort(files, 0, files.length-1);

//var maximumFiles = 10;
        var maximumFiles = files.length-1;

        fish.transferFunction = TransferFunction.flat(0.001);
        fish.transferFunction.addControlPoint(0.0, 0.0);
        fish.transferFunction.addControlPoint(0.01, 0.1);
        fish.transferFunction.addControlPoint(0.2, 0.1);
        fish.transferFunction.addControlPoint(0.3, 0.0);
        fish.transferFunction.addControlPoint(1.0, 0.0);
        fish.gamma = 1.8;

        sc.setColormap(fish, lut.loadLUT(lut.findLUTs().get("VirtualFishAssignment.lut")));

        for(i = 0; i < maximumFiles; i++) {
            var f = Paths.get(files[i]);
            var image = io.open(files[i]);
            sc.updateVolume(image, files[i], [fish.voxelSizeX, fish.voxelSizeY, fish.voxelSizeZ], fish);
            Thread.sleep(1000);
            sc.takeScreenshot();
            Thread.sleep(100);
        }*/

    }
}
