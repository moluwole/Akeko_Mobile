package com.yung_coder.oluwole.akeko

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.items.MaterialAboutTitleItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList


class About : MaterialAboutActivity() {
    override fun getMaterialAboutList(p0: Context): MaterialAboutList {
        var appCardBuilder = MaterialAboutCard.Builder()

        appCardBuilder.addItem(MaterialAboutTitleItem.Builder()
                .text(getString(R.string.app_name).toUpperCase())
                .icon(R.drawable.icon)
                .desc(getString(R.string.app_desc, "©"))
                .build())

        appCardBuilder.addItem(MaterialAboutActionItem.Builder()
                .text("Version")
                .subText("1.0.0")
                .icon(R.drawable.ic_info_outline_24dp)
                .build())

        appCardBuilder.addItem(MaterialAboutActionItem.Builder()
                .text(getString(R.string.developer_name))
                .subText(getString(R.string.developer_alias, "@"))
                .icon(R.drawable.ic_person_24dp)
                .build())

        appCardBuilder.addItem(MaterialAboutActionItem.Builder()
                .text(getString(R.string.github_text))
                .icon(R.drawable.ic_github_circle)
                .setOnClickAction {
                    val url = Uri.parse("https://github.com/MOluwole/Akeko_Mobile")
                    val intent = Intent(Intent.ACTION_VIEW, url)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
                .build())

        var authorCard = MaterialAboutCard.Builder()
        authorCard.title("Developers")

        authorCard.addItem(MaterialAboutActionItem.Builder()
                .text(getString(R.string.developer_name))
                .subText(getString(R.string.developer_alias, "@"))
                .icon(R.drawable.ic_twitter)
                .setOnClickAction {
                    val url = Uri.parse("https://twitter.com/MOluwole")
                    val intent = Intent(Intent.ACTION_VIEW, url)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
                .build())

        authorCard.addItem(MaterialAboutActionItem.Builder()
                .text("Azeez Abiodun Solomon")
                .subText("@iamhabbeboy")
                .icon(R.drawable.ic_github_circle)
                .setOnClickAction {
                    val url = Uri.parse("https://github.com/iamhabbeboy")
                    val intent = Intent(Intent.ACTION_VIEW, url)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
                .build())

        return MaterialAboutList(appCardBuilder.build(), authorCard.build())
    }

    override fun getActivityTitle(): CharSequence? {
        return "About"
    }
}
