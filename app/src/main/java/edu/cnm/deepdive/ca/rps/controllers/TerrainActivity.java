package edu.cnm.deepdive.ca.rps.controllers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import edu.cnm.deepdive.ca.rps.R;
import edu.cnm.deepdive.ca.rps.models.Terrain;
import edu.cnm.deepdive.ca.rps.views.TerrainView;

/**TerrainActivity class for Rock-paper-scissors Cellular Automaton.
 *
 * @author Abdul Haseeb Gauba
 */
public class TerrainActivity extends AppCompatActivity {

  /**Default rest time for threads class */
  private static final int RUNNER_THREAD_REST = 40;
  /**Default rest time for threads class */
  private static final int RUNNER_THREAD_SLEEP = 50;

  private boolean running = false;
  private boolean inForeground = false;
  private Terrain terrain = null;
  private TerrainView terrainView = null;
  private View terrainLayout;
  private Runner runner = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_terrain);
    initializeModel();
    initializeUserInterface();
  }

  @Override
  protected void onStart() {
    super.onStart();
    setInForeground(true);

  }

  @Override
  protected void onStop() {
    setInForeground(false);
    super.onStop();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.options, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    boolean running = isRunning();
    menu.findItem(R.id.run_item).setVisible(!running);
    menu.findItem(R.id.pause_item).setVisible(running);
    menu.findItem(R.id.reset_item).setEnabled(!running);
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.run_item:
        setRunning(true);
        break;
      case R.id.pause_item:
        setRunning(false);
        break;
      case R.id.reset_item:
        setInForeground(false);
        initializeModel();
        setInForeground(true);
        break;
      default:
        return super.onOptionsItemSelected(item);
    }
    invalidateOptionsMenu();
    return true;

  }

  /**
   * instantiates initializes a new Terrain object
   */
  private void initializeModel() {
    terrain = new Terrain();
    terrain.initialize();
  }

  /**
   * assigned values for terrainLayout and TerrainView
   */
  private void initializeUserInterface() {
    terrainLayout = findViewById(R.id.terrainLayout);
    terrainView = (TerrainView) findViewById(R.id.terrainView);

  }

  /**
   * returns the boolean value of the running field
   *
   * @return on/off indicator of running
   */
  private synchronized boolean isRunning() {
    return running;
  }

  /**
   * Sets the value of running used in the Runner class to determines if
   * terrain.step() and terrainView.setSource() are run.
   *
   * Allows update of the View determined upon the conditions in the Runner class.
   *
   * @param running on/off indicator of running
   */
  private synchronized void setRunning(boolean running) {
    this.running = running;
  }

  /**
   * Returns the currently specified value of inForeground field based whether the app is paused,
   * running or resetting.
   *
   * @return on/off indicator of inForground
   */

  private synchronized boolean isInForeground() {
    return inForeground;
  }

  /**
   * Sets inForeground value, used as an indicator to determine state(reset, pause or run)
   *
   * @param inForeground on/off indicator of inForground
   */
  private synchronized void setInForeground(boolean inForeground) {
    if (inForeground) {
      this.inForeground = true;
      if (runner == null){
        runner = new Runner();
        runner.start();
      }
      terrainLayout.post(new Runnable() {
        @Override
        public void run() {
          terrainView.setSource(terrain.getSnapshot());
        }
      });
    } else {
        this.inForeground = false;
        runner = null;
    }
  }

  /**
   * Determines the state of the thread and sets the sleep method to rest or sleep
   * based on the isRunning and isInForeground conditions
   */
  private class Runner extends Thread {

    @Override
    public void run() {
      while (isInForeground()) {
        while (isRunning() && isInForeground()) {
          terrain.step();
          terrainView.setSource(terrain.getSnapshot());
          try {
            Thread.sleep((RUNNER_THREAD_REST));
          } catch (InterruptedException ex) {
            //Do nothing.
          }
        }
        try {
          Thread.sleep(RUNNER_THREAD_SLEEP);
        } catch (InterruptedException ex) {
          //Do nothing
        }
      }
    }
  }
}
