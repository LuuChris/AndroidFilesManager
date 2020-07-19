package christopherluu.inc.filesmanager;

import java.util.List;
import java.util.ArrayList;
import java.lang.String;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Dialog;
import android.os.Environment;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.net.Uri;
import android.view.View;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.ContextMenu;


public class MainActivity extends ListActivity {

    private String path;
//    private List fileList = new ArrayList<>();
    private ListView list;
    TextView text;
    private List<String> myList = new ArrayList<>();
    File file,src,dst;
    File root= Environment.getExternalStorageDirectory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                text.setText(R.string.file_header);
                myList.clear();
                File[] list = root.listFiles();
                if(list!=null){
                    for( int i=0; i<list.length; i++)
                    {
                        if(!list[i].getName().startsWith(".")){
                            myList.add( list[i].getName() );
                        }
                    }
                }
                ArrayAdapter set = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, myList);
                setListAdapter(set);

            }
        });

        text=(TextView)findViewById(R.id.textView);
        myList = new ArrayList<>();

        File[] list = root.listFiles();

        if(list!=null){
            for( int i=0; i<list.length; i++)
            {
                if(!list[i].getName().startsWith(".")){
                    myList.add( list[i].getName() );
                }
            }
        }

        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList ));

        final Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.create_file,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(view);
                final Dialog dialog = builder.show();
                final EditText folderName = view.findViewById(R.id.editText);
                Button cancel = view.findViewById(R.id.button4);
                cancel.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        dialog.dismiss();
                    }
                });

                Button create = view.findViewById(R.id.button3);
                create.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view){
                        if(!(folderName.getText().toString()).equals("") ){
                            File f = new File(root.toString()+"/"+folderName.getText().toString());

                            if(f.exists()){
                                Toast.makeText(getApplicationContext(), "Folder already exists", Toast.LENGTH_SHORT).show();
                            } else if(!f.mkdir()) {
                                Toast.makeText(getApplicationContext(), "Folder can't be created in this directory", Toast.LENGTH_SHORT).show();
                            } else {
                                f.mkdir();
                                Toast.makeText(getApplicationContext(), f+" is created", Toast.LENGTH_SHORT).show();
                                myList.add(folderName.getText().toString());
                                ArrayAdapter set = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, myList);
                                setListAdapter(set);

                                dialog.dismiss();
                            }

                        }else{

                             Toast.makeText(getApplicationContext(), "Must input a name", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });


        registerForContextMenu(findViewById(android.R.id.list));
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);


        File temp_file = new File( file, myList.get( position ) );

        if( !temp_file.isFile()) {
            text.setText(myList.get(position));
            file = new File(file, myList.get(position));
            File[] list = file.listFiles();

            myList.clear();

            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    if (!list[i].getName().startsWith(".")) {
                        myList.add(list[i].getName());
                    }
                }
            }

            setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myList));

        } else if( temp_file.toString().endsWith(".jpg") || temp_file.toString().endsWith(".png")){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(temp_file), "image/*");
            startActivity(intent);

        } else if( temp_file.toString().endsWith(".pdf")){
            Uri path = Uri.fromFile(temp_file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setDataAndType(path, "*/*");
            try {
                startActivity(intent);
            }
            catch (ActivityNotFoundException e) {
                Toast.makeText(this, temp_file.toString()+" ERROR", Toast.LENGTH_SHORT).show();

            }
        } else if( temp_file.toString().endsWith(".txt") || temp_file.toString().endsWith(".doc")){
            Uri path = Uri.fromFile(temp_file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setDataAndType(path, "*/*");
            try {
                startActivity(intent);
            }
            catch (ActivityNotFoundException e) {
                Toast.makeText(this, temp_file.toString() + " ERROR", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, temp_file.toString()+" is not a directory", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId() == android.R.id.list){
            menu.add(0,0,0,getText(R.string.delete));
            menu.add(0,1,0,"Copy");
            menu.add(0,2,0,"Paste");

        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        list = (ListView)findViewById(android.R.id.list);
        AdapterView.AdapterContextMenuInfo a = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String k = ((TextView) a.targetView).getText().toString();

        if(item.getItemId() == 0){

            try{

                for(File f : file.listFiles()){
                    if(f.getName().equals(k) && f.delete() && !k.equals("sdcard")){
                        f.delete();
                        Toast.makeText(getApplicationContext(), f.toString()+" deleted ", Toast.LENGTH_SHORT).show();
                        File[] list = file.listFiles();
                        myList.clear();
                        if (list != null) {
                            for (int i = 0; i < list.length; i++) {
                                if (!list[i].getName().startsWith(".")) {
                                    myList.add(list[i].getName());
                                }
                            }
                        }
                        ArrayAdapter set = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, myList);
                        setListAdapter(set);
                        break;

                    }
                    if(f.isDirectory()){
                        String[] children = f.list();

                        for (int i = 0; i < children.length; i++)
                        {
                            new File(f, children[i]).delete();
                        }
                        f.delete();

                        Toast.makeText(getApplicationContext(), "Directory is deleted ", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(f.getName().equals(k) && !f.delete()){
                        Toast.makeText(getApplicationContext(), k+" cannot be deleted ", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

            }catch (Exception e) {
                Toast.makeText(this, "Cannot Delete", Toast.LENGTH_SHORT).show();
            }

        } else if(item.getItemId() == 1){
            for(File f : file.listFiles()){
                if(f.getName().equals(k)){
                    src=f;

                    Toast.makeText(getApplicationContext(), f.toString()+" COPIED "+src.toString(), Toast.LENGTH_SHORT).show();


                }

            }

        } else if(item.getItemId() == 2){

            if(src!=null) {

                for (File f : file.listFiles()) {
                    if (f.getName().equals(k)) {
                        dst = f;
                        copy(src.toString(), dst.toString());
                        Toast.makeText(getApplicationContext(), src.toString() + " COPIED to destination ", Toast.LENGTH_SHORT).show();


                    }

                }

            }else{
                Toast.makeText(getApplicationContext(), "Nothing Copied", Toast.LENGTH_SHORT).show();
            }


        } else {


        }
        return true;
    }

 public static void copy(String srcDir, String dstDir) {

     try {
         File src = new File(srcDir);
         File dst = new File(dstDir, src.getName());

         if (src.isDirectory()) {

             String files[] = src.list();
             int filesLength = files.length;
             for (int i = 0; i < filesLength; i++) {
                 String src1 = (new File(src, files[i]).getPath());
                 String dst1 = dst.getPath();
                 copy(src1, dst1);

             }
         } else {
             if (!dst.getParentFile().exists())
                 dst.getParentFile().mkdirs();

             if (!dst.exists()) {
                 dst.createNewFile();
             }

             FileChannel source = null;
             FileChannel destination = null;

             try {
                 source = new FileInputStream(src).getChannel();
                 destination = new FileOutputStream(dst).getChannel();
                 destination.transferFrom(source, 0, source.size());
             } finally {
                 if (source != null) {
                     source.close();
                 }
                 if (destination != null) {
                     destination.close();
                 }
             }
         }
     }catch(IOException e){

     }
 }


}
