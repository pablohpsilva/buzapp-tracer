package aloeio.buzapp_tracer.app.Utils.UI;

import aloeio.buzapp_tracer.app.R;
import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by pablohenrique on 3/15/15.
 */
public class MyDialog implements Cloneable{
    private Dialog dialog = null;
    private TextView txt_title = null;
    private TextView txt_message = null;
    private Button btn_ok = null;
    private Button btn_maybe = null;
    private Button btn_cancel = null;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public MyDialog copy() throws CloneNotSupportedException{
        return (MyDialog) this.clone();
    }

    public MyDialog(){
    }

    public MyDialog(Context context){
        setContext(context);
    }

    public MyDialog setContext(Context context){
        this.close();
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.template_dialog);
        txt_title = (TextView) dialog.findViewById(R.id.dialog_txt_head);
        txt_message = (TextView) dialog.findViewById(R.id.dialog_txt_body);
        btn_ok = (Button) dialog.findViewById(R.id.dialog_btn_ok);
        btn_maybe = (Button) dialog.findViewById(R.id.dialog_btn_maybe);
        btn_cancel = (Button) dialog.findViewById(R.id.dialog_btn_cancel);
        return this;
    }

    public void show(){
        dialog.show();
    }

    public void close(){
        if(dialog != null) {
            this.makeInvisible(btn_maybe);
            this.makeInvisible(btn_cancel);
            dialog.dismiss();
        }
        dialog = null;
    }

    public MyDialog setTitle(String title){
        txt_title.setText(title);
        return this;
    }

    public MyDialog setMessage(String message){
        txt_message.setText(message);
        return this;
    }

    public MyDialog setMessageWithLink(String preMessage, String url, String label, String postMessage){
        txt_message.setText(Html.fromHtml(preMessage + "<a href=\""+url+"\">"+label+"</a> " + postMessage));
        txt_message.setMovementMethod(LinkMovementMethod.getInstance());
        return this;
    }

    public MyDialog setPositiveButton(String name, View.OnClickListener listener){
        return setButton(name, listener, btn_ok);
    }

    public MyDialog setNeutralButton(String name, View.OnClickListener listener){
        return setButton(name, listener, btn_maybe);
    }

    public MyDialog setNegativeButton(String name, View.OnClickListener listener){
        return setButton(name, listener, btn_cancel);
    }

    private MyDialog setButton(String name, View.OnClickListener listener, Button btn){
        btn.setText(name);
        btn.setOnClickListener(listener);
        this.makeVisible(btn);
        return this;
    }

    private void makeVisible(Button btn){
        btn.setVisibility(View.VISIBLE);
    }

    private void makeInvisible(Button btn){
        btn.setVisibility(View.GONE);
    }
}
