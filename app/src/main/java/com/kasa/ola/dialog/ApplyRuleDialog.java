package com.kasa.ola.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kasa.ola.R;
import com.kasa.ola.manager.Const;
import com.kasa.ola.manager.LoginHandler;
import com.kasa.ola.ui.WebActivity;

public class ApplyRuleDialog extends Dialog {

    public ApplyRuleDialog(@NonNull Context context) {
        super(context, R.style.custom_dialog_style);
    }

    public ApplyRuleDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
    private ApplyRuleDialog.DialogInterface dialogInterface;
    public interface DialogInterface {
        void disagree(ApplyRuleDialog dialog);
        void agree(ApplyRuleDialog dialog);
    }



    public static class Builder {

        private Context context;
        private DialogInterface dialogInterface;
        private String contentText ="";
        private String leftButtonText;
        private String rightButtonText;
        private String title;
        public Builder(Context context) {
            this.context = context;
        }

        public Builder setDialogInterface(DialogInterface dialogInterface) {
            this.dialogInterface = dialogInterface;
            return this;
        }
        public Builder setMessage(String contentText) {
            this.contentText = contentText;
            return this;
        }
        public Builder setLeftButton(String leftButtonText) {
            this.leftButtonText = leftButtonText;
            return this;
        }
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
        public Builder setRightButton(String rightButtonText) {
            this.rightButtonText = rightButtonText;
            return this;
        }
        public ApplyRuleDialog create() {
            final ApplyRuleDialog privateDialog = new ApplyRuleDialog(context);
            privateDialog.setContentView(R.layout.view_apply_rule_dialog);
            TextView tv_rule_click_area = privateDialog.findViewById(R.id.tv_rule_click_area);
            TextView tv_rule = privateDialog.findViewById(R.id.tv_rule);
            TextView tv_rule_click_footer = privateDialog.findViewById(R.id.tv_rule_click_footer);
            TextView tv_rule_title = privateDialog.findViewById(R.id.tv_rule_title);
            TextView btn_cancel = privateDialog.findViewById(R.id.btn_cancel);
            TextView btn_confirm = privateDialog.findViewById(R.id.btn_confirm);
            if (!TextUtils.isEmpty(title)){
                tv_rule_title.setVisibility(View.VISIBLE);
                tv_rule_title.setText(title);
            }else {
                tv_rule_title.setVisibility(View.GONE);
            }
            String text = context.getString(R.string.private_content_first)+context.getString(R.string.private_content_second)
                    +context.getString(R.string.private_content_third)+context.getString(R.string.private_content_fourth)+",";
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            int start1 = context.getString(R.string.private_content_first).length();
            int end1 = (context.getString(R.string.private_content_first)+context.getString(R.string.private_content_second)).length();
            int start2 = (context.getString(R.string.private_content_first)+context.getString(R.string.private_content_second)+context.getString(R.string.private_content_third)).length();
            int end2 = (context.getString(R.string.private_content_first)+context.getString(R.string.private_content_second)+context.getString(R.string.private_content_third)+context.getString(R.string.private_content_fourth)).length();
            // 设置点击监听
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(context, WebActivity.class);
                    intent.putExtra(Const.WEB_TITLE, context.getString(R.string.register_agreement));
                    intent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getUserProUrl());
                    context.startActivity(intent);
                }
                //去掉下划线，重新updateDrawState并且setUnderlineText(false)
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            }, start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.COLOR_FF1E90FF)),start1,end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(context, WebActivity.class);
                    intent.putExtra(Const.WEB_TITLE, context.getString(R.string.privacy_ensure_agreement));
                    intent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getPrivacyEnsureUrl());
                    context.startActivity(intent);
                }
                //去掉下划线，重新updateDrawState并且setUnderlineText(false)
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            }, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.COLOR_FF1E90FF)),start2,end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_rule_click_area.setText(builder);
            tv_rule_click_area.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
            tv_rule_click_area.setMovementMethod(LinkMovementMethod.getInstance());  // 设置TextView为可点击状态

            String message =/*context.getString(R.string.private_content_first)+"<font color='#1E90FF'>"+context.getString(R.string.private_content_second)+"</font>"
                    +context.getString(R.string.private_content_third)+"<font color='#1E90FF'>"+context.getString(R.string.private_content_fourth)+"</font>"+","+"<br/>"
                    +*/context.getString(R.string.private_content_fifth)+"<br/>"+context.getString(R.string.private_content_fifth_1)+"<br/>"+context.getString(R.string.private_content_sixth)
                    /*+"<br/>"+context.getString(R.string.private_content_seventh)+"<font color='#1E90FF'>"+context.getString(R.string.private_content_eighth)+"</font>"
                    +context.getString(R.string.private_content_ninth)*/
                    ;
            tv_rule.setText(Html.fromHtml(message));

            String textFooter = context.getString(R.string.private_content_seventh)+context.getString(R.string.private_content_eighth)
                    +context.getString(R.string.private_content_ninth);
            SpannableStringBuilder footerBuilder = new SpannableStringBuilder(textFooter);
            int start3 = context.getString(R.string.private_content_seventh).length();
            int end3 = (context.getString(R.string.private_content_seventh)+context.getString(R.string.private_content_eighth)).length();
            footerBuilder.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(context, WebActivity.class);
                    intent.putExtra(Const.WEB_TITLE, context.getString(R.string.privacy_ensure_agreement));
                    intent.putExtra(Const.INTENT_URL, LoginHandler.get().getBaseUrlBean().getPrivacyEnsureUrl());
                    context.startActivity(intent);
                }
                //去掉下划线，重新updateDrawState并且setUnderlineText(false)
                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            }, start3, end3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            footerBuilder.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.COLOR_FF1E90FF)),start3,end3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_rule_click_footer.setText(footerBuilder);
            tv_rule_click_footer.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
            tv_rule_click_footer.setMovementMethod(LinkMovementMethod.getInstance());  // 设置TextView为可点击状态

            if (null != leftButtonText && !TextUtils.isEmpty(leftButtonText)) {
                btn_cancel.setText(leftButtonText);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != dialogInterface) {
                            dialogInterface.disagree(privateDialog);
                        }
                    }
                });
            } else {
                btn_cancel.setVisibility(View.GONE);
            }
            if (null != rightButtonText && !TextUtils.isEmpty(rightButtonText)) {
                btn_confirm.setText(rightButtonText);
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != dialogInterface) {
                            dialogInterface.agree(privateDialog);
                        }
                    }
                });
            } else {
                btn_confirm.setVisibility(View.GONE);
            }

            return privateDialog;
        }

    }
}
