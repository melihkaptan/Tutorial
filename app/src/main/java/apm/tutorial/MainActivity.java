package apm.tutorial;

import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import weka.associations.Apriori;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import  weka.core.Instance;
import weka.core.Instances;
import  weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils;


public class MainActivity extends ActionBarActivity {

    public EditText editText;
    public TextView textView;
    public Button save, load;
    public CropImageView cropImageView;
    public ImageView imageView;

    public String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aaTutorial";

    private  Instance inst_co ;

    MainActivity mainActivity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);
        save = (Button) findViewById(R.id.save);
        load = (Button) findViewById(R.id.load);
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        imageView = (ImageView) findViewById(R.id.imageView);

        File dir = new File(path);
        if(!dir.exists()){
        dir.mkdirs();}

        cropImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mala));





    }

    public void buttonSave (View view)
    {
        cropImageView.setVisibility(View.INVISIBLE);
        imageView.setImageBitmap(  cropImageView.getCroppedImage() );
    }

    public void buttonLoad (View view)
    {
        File file = new File (path + "/weather.txt");
        File wekaFile = new File(path + "/weather1.arff");
        File woteArff = new File(path + "/soybean.arff");
        voteArff(woteArff,mainActivity,path+"/vote.arff");
        //Load(file,wekaFile,mainActivity,path);
        //deneme1
    }


    public static void voteArff(File wFile,MainActivity m, String path )
    {

        try {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(path);
        Instances data = source.getDataSet();
        Apriori model = new Apriori();
        model.buildAssociations(data);
            String string = model.toString();
            String[] parts = string.split("Best rules found:\n\n");
            String[] rules = parts[1].split("\n");

            for(int i=0;i<rules.length;i++)
            {
                System.out.println("parts: " + rules[i]);
            }



    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

    }


    public static void Load(File file,File wFile,MainActivity m, String path )
    {
        ArffLoader arffLoader=new ArffLoader();
        Instances structre, st_Trees;
        Instance current;



        try {
            st_Trees = new Instances("Training Instance", m.attribute_return(), 0);
            arffLoader.setFile(wFile);
            structre=arffLoader.getStructure();
            structre.setClassIndex(structre.numAttributes() - 1);
            J48 tree= new J48();
            NaiveBayes naiveBayes = new NaiveBayes();
            naiveBayes.buildClassifier(structre);


            while ((current=arffLoader.getNextInstance(structre))!=null)
            {
               // tree.buildClassifier(structre);
                st_Trees.add(current);
            }
            Log.d("structer is", "" + structre.numAttributes());
            st_Trees.setClassIndex(st_Trees.numAttributes() - 1);
            naiveBayes.buildClassifier(st_Trees);
            tree.buildClassifier(st_Trees);
             System.out.println("Naive Bayes is:\n" + tree);

             double d =  m.Classify("rainy", 71, 74, "TRUE" ,path);
            //weka.core.SerializationHelper.write(path+"/j48.model", tree);
           //weka.core.SerializationHelper.write(path+"/naivebayes.model", naiveBayes);

          // Log.d("AAA:"+nb.classifyInstance(current),"--]");


        } catch (Exception e) {
            Log.d("Bir Hata","Lool ",e);
        }

    }


    public double Classify(String outLook,double temp, double hum,String wind , String path)  {
   /* Instance instance;
        String s;
        ArrayList<Attribute> atts = new ArrayList<Attribute>();
        ArrayList<String> classVal = new ArrayList<String>();
        classVal.add("yes");
        classVal.add("no");
        List outlook_list = new ArrayList(3);
        outlook_list.add("sunny");
        outlook_list.add("overcast");
        outlook_list.add("rainy");
        Attribute outlook = new Attribute("outlook", outlook_list);
        Attribute tempeture = new Attribute("tempeture");
        Attribute humidity = new Attribute("humidity");
        List windy_list = new ArrayList(3);
        windy_list.add("TRUE");
        windy_list.add("FALSE");
        Attribute windy = new Attribute("windy", windy_list);
        atts.add(outlook);
        atts.add(tempeture);
        atts.add(humidity);
        atts.add(windy);
        atts.add(new Attribute("@@class@@", classVal));

        Instances dataRaw = new Instances("TestInstances", atts, 0);
        dataRaw.setClassIndex(dataRaw.numAttributes() - 1);
        double[] instanceValue1 = new double[]{2,70,80,0,0};
        dataRaw.add(instance=new DenseInstance(1.0, instanceValue1));

        // Create attributes to be used with classifiers
        // Test the model
        double result = -1;


        Instances dataUnlabeled = new Instances("TestInstances", atts, 0);
        dataUnlabeled.add(instance);
        dataUnlabeled.setClassIndex(dataUnlabeled.numAttributes() - 1);
        NaiveBayesUpdateable naiveBayesUpdateable = null;
        try {
            naiveBayesUpdateable = (NaiveBayesUpdateable) weka.core.SerializationHelper.read(path+"/naive_bayes.model");
            result = naiveBayesUpdateable.classifyInstance(dataUnlabeled.instance(0));
             s="Result is" + naiveBayesUpdateable.globalInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }


*/
        double result = -1;
        double result1=result;
        try {

            ArrayList<Attribute> atts = new ArrayList<Attribute>();
            ArrayList<String> classVal = new ArrayList<String>();
            classVal.add("yes");
            classVal.add("no");
            List outlook_list = new ArrayList(3);
            outlook_list.add("sunny");
            outlook_list.add("overcast");
            outlook_list.add("rainy");
            Attribute outlook = new Attribute("outlook", outlook_list);
            Attribute temperature = new Attribute("temperature");
            Attribute humidity = new Attribute("humidity");
            List windy_list = new ArrayList(3);
            windy_list.add("TRUE");
            windy_list.add("FALSE");
            Attribute windy = new Attribute("windy", windy_list);
            atts.add(outlook);
            atts.add(temperature);
            atts.add(humidity);
            atts.add(windy);
            atts.add(new Attribute("@@class@@", classVal));

            Instances dataRaw = new Instances("TestInstances", atts, 0);
            dataRaw.setClassIndex(dataRaw.numAttributes() - 1);

            // Create instances for each pollutant with attribute values latitude,
            // longitude and pollutant itself
            inst_co = new DenseInstance(dataRaw.numAttributes());
            // Set instance's values for the attributes "latitude", "longitude", and
            // "pollutant concentration"
            inst_co.setValue(outlook , outLook);
            inst_co.setValue(temperature, temp);
            inst_co.setValue(humidity, hum);
            inst_co.setValue(windy, wind);
            inst_co=inst_co;
             //inst_co.setMissing(cluster);
            dataRaw.add(inst_co);
            dataRaw.setClassIndex(dataRaw.numAttributes() - 1);
            J48 j48 = (J48) weka.core.SerializationHelper.read(path+"/J48.model");
            NaiveBayes naiveBayes = (NaiveBayes) weka.core.SerializationHelper.read(path+"/naivebayes.model");
            Log.d("oOo", "->" + j48.graph());

            Instances trees=new Instances("Trainig Instances" , atts ,0);
            trees.setClassIndex(dataRaw.numAttributes() - 1);
            trees.add(inst_co);

            System.out.println("Class name is " + trees.classAttribute().value((int) j48.classifyInstance(trees.instance(0))));
            System.out.println("Class name is "+trees.classAttribute().value((int) naiveBayes.classifyInstance(trees.instance(0))));
            result = j48.classifyInstance(trees.instance(0));
            result1 = naiveBayes.classifyInstance(trees.instance(0));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }




    public ArrayList<Attribute> attribute_return(){


    ArrayList<Attribute> atts = new ArrayList<Attribute>();
    ArrayList<String> classVal = new ArrayList<String>();
    classVal.add("no");
    classVal.add("yes");
    List outlook_list = new ArrayList(3);
    outlook_list.add("sunny");
    outlook_list.add("overcast");
    outlook_list.add("rainy");
    Attribute outlook = new Attribute("outlook", outlook_list);
    Attribute temperature = new Attribute("temperature");
    Attribute humidity = new Attribute("humidity");
    List windy_list = new ArrayList(3);
    windy_list.add("FALSE");
    windy_list.add("TRUE");
    Attribute windy = new Attribute("windy", windy_list);
    atts.add(outlook);
    atts.add(temperature);
    atts.add(humidity);
    atts.add(windy);
    atts.add(new Attribute("@@class@@", classVal));
        return atts;
}










    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
