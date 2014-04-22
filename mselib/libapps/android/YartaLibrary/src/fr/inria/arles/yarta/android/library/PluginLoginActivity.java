package fr.inria.arles.yarta.android.library;

import fr.inria.arles.yarta.R;
import fr.inria.arles.yarta.android.library.plugins.Plugin;
import fr.inria.arles.yarta.android.library.plugins.PluginManager;
import fr.inria.arles.yarta.android.library.util.JobRunner.Job;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * General login activity for Yarta Library plugins.
 */
public class PluginLoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pluginlogin);

		webContainer = (WebView) findViewById(R.id.webContainer);
		webContainer.setWebViewClient(client);

		WebSettings settings = webContainer.getSettings();

		settings.setJavaScriptEnabled(true);
		settings.setAppCacheEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);

		currentPlugin = PluginManager.getInstance().getSelectedPlugin();

		execute(new Job() {
			String authURL;

			@Override
			public void doWork() {
				authURL = currentPlugin.getAuthorizationURL();
			}

			@Override
			public void doUIAfter() {
				webContainer.loadUrl(authURL);
			}
		});
	}

	/**
	 * The client used in the WebView container.
	 */
	private WebViewClient client = new WebViewClient() {

		public void onPageFinished(WebView view, final String url) {
			log("onPageFinished %s", url);
			
			execute(new Job() {
				boolean success;
				
				public void doWork() {
					success = currentPlugin.authorize(url);
				}
				public void doUIAfter() {
					if (success) {
						finish();
					}
				}
			});
		}
	};

	private WebView webContainer;
	private Plugin currentPlugin;
}
