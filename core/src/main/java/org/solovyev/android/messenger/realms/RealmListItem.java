package org.solovyev.android.messenger.realms;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.solovyev.android.list.ListAdapter;
import org.solovyev.android.list.ListItem;
import org.solovyev.android.messenger.R;
import org.solovyev.android.messenger.RealmConfigurationActivity;
import org.solovyev.android.view.ViewFromLayoutBuilder;

public class RealmListItem implements ListItem {

    @NotNull
    private static final String TAG_PREFIX = "realm_list_item_view_";

    @NotNull
    private Realm realm;

    public RealmListItem(@NotNull Realm realm) {
        this.realm = realm;
    }

    @Nullable
    @Override
    public OnClickAction getOnClickAction() {
        return new OnClickAction() {
            @Override
            public void onClick(@NotNull Context context, @NotNull ListAdapter<? extends ListItem> adapter, @NotNull ListView listView) {
                RealmConfigurationActivity.startForEditRealm(context, realm);
            }
        };
    }

    @Nullable
    @Override
    public OnClickAction getOnLongClickAction() {
        return null;
    }

    @NotNull
    @Override
    public View updateView(@NotNull Context context, @NotNull View view) {
        if (String.valueOf(view.getTag()).startsWith(TAG_PREFIX)) {
            fillView((ViewGroup) view, context);
            return view;
        } else {
            return build(context);
        }
    }

    @NotNull
    @Override
    public View build(@NotNull Context context) {
        final ViewGroup view = (ViewGroup) ViewFromLayoutBuilder.newInstance(R.layout.msg_list_item_realm).build(context);
        fillView(view, context);
        return view;
    }

    @NotNull
    private String createTag() {
        return TAG_PREFIX + realm.getId();
    }

    private void fillView(@NotNull final ViewGroup view, @NotNull final Context context) {
        final String tag = createTag();

        if (!tag.equals(view.getTag())) {
            view.setTag(tag);

            final ImageView realmIconImageView = (ImageView) view.findViewById(R.id.mpp_realm_icon_imageview);
            final Drawable realmIcon = context.getResources().getDrawable(realm.getRealmDef().getIconResId());
            realmIconImageView.setImageDrawable(realmIcon);

            final TextView realmNameTextView = (TextView) view.findViewById(R.id.mpp_realm_name_textview);
            realmNameTextView.setText(realm.getDisplayName(context));
        }
    }

    public void onRealmChangedEvent(@NotNull RealmChangedEvent event) {
        if ( event.getRealm().equals(realm)) {
            this.realm = event.getRealm();
        }
    }
}
