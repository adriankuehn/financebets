package aaa.financebets.alpha_v1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.core.text.HtmlCompat
import com.example.alpha_v1.R


class Lizenzen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lizenzen)

        val actionBar: ActionBar? = supportActionBar
        if (actionBar != null) { actionBar.setDisplayHomeAsUpEnabled(true) }

        val tv_datenschutz : TextView = findViewById(R.id.tv_lizenzen)
        tv_datenschutz.setMovementMethod(ScrollingMovementMethod())

        var htmlText = "<strong>JetBrains - Kotlin:</strong> <br>\n" +
                "<a href=https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt>https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt</a> <p>\n" +
                "\n" +
                "<strong>androidx:</strong> <br>\n" +
                "<a href=https://github.com/androidx/androidx/blob/androidx-main/LICENSE.txt>https://github.com/androidx/androidx/blob/androidx-main/LICENSE.txt</a> <p>\n" +
                "\n" +
                "<strong>Appcombat:</strong> <br>\n" +
                "<a href=https://github.com/androidx-releases/Appcompat/blob/master/LICENSE>https://github.com/androidx-releases/Appcompat/blob/master/LICENSE</a> <p>\n" +
                "\n" +
                "<strong>com.google.android.material:</strong> <br>\n" +
                "<a href=https://github.com/material-components/material-components-android/blob/master/LICENSE>https://github.com/material-components/material-components-android/blob/master/LICENSE</a> <p>\n" +
                "\n" +
                "<strong>Google - Firebase:</strong> <br>\n" +
                "<a href=https://github.com/firebase/firebase-android-sdk/blob/master/LICENSE>https://github.com/firebase/firebase-android-sdk/blob/master/LICENSE</a> <p>\n" +
                "\n" +
                "<strong>GraphView by jjoe64:</strong> <br>\n" +
                "<a href=https://github.com/jjoe64/GraphView/blob/master/license.txt>https://github.com/jjoe64/GraphView/blob/master/LICENSE.txt</a> <p>"
        val htmlToTextView : TextView = findViewById(R.id.tv_lizenzen)
        htmlToTextView.setMovementMethod(ScrollingMovementMethod())
        htmlToTextView.text = HtmlCompat.fromHtml(htmlText, 0)
        htmlToTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return true
    }
}