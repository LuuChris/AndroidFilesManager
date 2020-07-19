package christopherluu.inc.filesmanager;
import java.util.List;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class ListAdapter extends ArrayAdapter<RunningAppProcessInfo> { // List context
    private final Context context;
    // List values
    private final List<RunningAppProcessInfo> values;
    public ListAdapter(Context context, List<RunningAppProcessInfo> values) {
        super(context, R.layout.activity_main, values);
        this.context = context;
        this.values = values;
    }
}
