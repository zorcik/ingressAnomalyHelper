package weblogic.pl.ingressanomalyhelper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class Portals extends Activity {

    public static int currentCluster = 1;
    PortalAdapter dataAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentCluster = getIntent().getExtras().getInt("currentCluster");

        setContentView(R.layout.activity_portals);
        displayListView();
    }

    public String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }

    private void displayListView() {

        Log.d("INGRR", "Dodaję listę");
        //Array list of countries
        ArrayList<Portal> portalsList = new ArrayList<Portal>();

        InputStream ins = getResources().openRawResource(
                getResources().getIdentifier("cluster"+currentCluster,
                        "raw", getPackageName()));
        String json = readTextFile(ins);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.prefKey), Context.MODE_PRIVATE);
        try {
            JSONArray jArray = new JSONArray(json);
            int length = jArray.length();
            Log.d("INGRR", "LENGTH: "+length);
            for (int a=0; a<length; a++)
            {
                JSONObject oneObject = jArray.getJSONObject(a);

                String t = "Cluster"+Portals.currentCluster+"-Portal"+a;
                boolean showPortal = sharedPref.getBoolean(t, true);

                //Log.d("INGGR", "Nazwa (2): "+t+": "+(showPortal ? "true" : "false"));

                String name = oneObject.getString("name");
                Portal p = new Portal(a, name, showPortal);

                portalsList.add(p);
            }
        }
        catch (Exception e)
        {
            Log.e("INGRR", "ERROR Parsing Data: "+e.getMessage());
        }

        //create an ArrayAdaptar from the String Array
        dataAdapter = new PortalAdapter(this,
                R.layout.portal_item, portalsList);
        ListView listView = (ListView) findViewById(R.id.listView1);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_portals, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_check_all) {
            checkAllItems(true);
            return true;
        }
        if (id == R.id.action_uncheck_all) {
            checkAllItems(false);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkAllItems(boolean checked)
    {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.prefKey), Context.MODE_PRIVATE);
        ListView listView = (ListView) findViewById(R.id.listView1);
        int items = listView.getAdapter().getCount();
        PortalAdapter adapter = (PortalAdapter)listView.getAdapter();
        SharedPreferences.Editor editor = sharedPref.edit();
        for (int indeks=0; indeks < items; indeks++)
        {
            View v = listView.getAdapter().getView(indeks, null, listView);
            PortalAdapter.ViewHolder holder = (PortalAdapter.ViewHolder) v.getTag();
            Portal portal = (Portal)holder.name.getTag();
            Log.v("INGREE", "Portal: "+portal.getName());
            holder.name.setChecked(checked);
            portal.setSelected(checked);
            String t = "Cluster"+Portals.currentCluster+"-Portal"+portal.getCode();
            editor.putBoolean(t, checked);
            editor.commit();
            Portal Portal = adapter.PortalList.get(indeks);
            Portal.setSelected(checked);
        }
        adapter.notifyDataSetChanged ();
    }

    public class PortalAdapter extends ArrayAdapter<Portal> {

        public ArrayList<Portal> PortalList;

        public PortalAdapter(Context context, int textViewResourceId,
                             ArrayList<Portal> PortalList) {
            super(context, textViewResourceId, PortalList);
            this.PortalList = new ArrayList<Portal>();
            this.PortalList.addAll(PortalList);
        }

        private class ViewHolder {
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.portal_item, null);

                holder = new ViewHolder();
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Portal portal = (Portal) cb.getTag();
                        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.prefKey), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        String t = "Cluster"+Portals.currentCluster+"-Portal"+portal.getCode();
                        Log.d("INGGR", "Nazwa: "+t);
                        editor.putBoolean(t, cb.isChecked());
                        portal.setSelected(cb.isChecked());
                        editor.commit();
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Portal Portal = PortalList.get(position);
            holder.name.setText(Portal.getName());
            holder.name.setChecked(Portal.isSelected());
            holder.name.setTag(Portal);

            return convertView;

        }

    }
}
