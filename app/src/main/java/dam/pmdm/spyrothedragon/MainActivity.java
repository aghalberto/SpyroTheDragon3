package dam.pmdm.spyrothedragon;

import static android.view.View.GONE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.window.SplashScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;
import dam.pmdm.spyrothedragon.databinding.GuideBinding;
import dam.pmdm.spyrothedragon.databinding.GuideMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private GuideBinding guideBinding;
    private GuideMainBinding guideMainBinding;
    private boolean needToStartGuide;
    NavController navController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Splash screen
        //SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //Instanciamos guideBinding
        guideBinding = binding.includeLayout;
        guideMainBinding = binding.includeMainGuideLayout;
        setContentView(binding.getRoot());


        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navController = NavHostFragment.findNavController(navHostFragment);
            NavigationUI.setupWithNavController(binding.navView, navController);
            NavigationUI.setupActionBarWithNavController(this, navController);
        }

        binding.navView.setOnItemSelectedListener(this::selectedBottomMenu);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_characters ||
                    destination.getId() == R.id.navigation_worlds ||
                    destination.getId() == R.id.navigation_collectibles) {
                // Para las pantallas de los tabs, no queremos que aparezca la flecha de atrás
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                // Si se navega a una pantalla donde se desea mostrar la flecha de atrás, habilítala
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        });

        //Cargamos las preferencias; importante para needToStartGuide
        cargarPreferencias();

        //Inicializamos la guía
        //PARA AHORRAR TIEMPO ponemos el needToStartGuide a true siempre
        needToStartGuide = true;

        //Iniciamos la página principal de la guía, tiene un botón que lleva a la guía normal
        initializeMainGuide();

        //initializeGuide();


    }


    /**
     * Carga las preferencias en variables de clase
     */
    private void cargarPreferencias() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Cargamos needToStartGuide por defecto a true
        needToStartGuide = sharedPreferences.getBoolean("needToStartGuide", true);
    }

    /**
     * Inicializamos la página principal de la guía. Se llama en onCreate.
     */
    private void initializeMainGuide() {
        //Ponemos un listener al botón de abrir guía
        guideMainBinding.buttonOpenGuide.setOnClickListener(this::initializeGuide);
        if (needToStartGuide) {
            //Si tenemos algún menú lateral, hay que bloquearlo

            //Ocultamos las vistas principal y guía paginada
            binding.constraintLayout.setVisibility(View.GONE);
            guideBinding.guideLayout.setVisibility(View.GONE);

            //Mostramos la vista de la mainGuide
            guideMainBinding.guideMain.setBackgroundColor(View.VISIBLE);

        }
    }


    /**
     * Inicializamos la guía en sí. Cada click en la capa cambiará de página
     */
    private void initializeGuide(View view) {
        //onClickListener del botón exitGuide
        guideBinding.exitguide.setOnClickListener(this::onExitGuide);

        //Si tenemos algún menú lateral, hay que bloquearlo

        //Ocultamos la vista principal y la guía main
        binding.constraintLayout.setVisibility(View.GONE);
        guideMainBinding.guideMain.setVisibility(View.GONE);


        //Ponemos la guía a visible
        guideBinding.guideLayout.setVisibility(View.VISIBLE);
        //Con el object animator creamos unas animaciones
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(
                guideBinding.pulseImage, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(
                guideBinding.pulseImage, "scaleY", 1f, 0.5f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(
                guideBinding.textStep, "alpha", 0f, 1f);

        //Animaciones.
        //Ejecutamos el escalado/desescalado
        scaleX.setRepeatCount(3);
        scaleY.setRepeatCount(3);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY).before(fadeIn);
        animatorSet.setDuration(1000);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                if (needToStartGuide) {
                    super.onAnimationEnd(animation);
                    //Abrimos el siguiente binding
                    guideBinding.pulseImage.setVisibility(View.GONE);
                    guideBinding.textStep.setVisibility(View.VISIBLE);
                    guideBinding.exitguide.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    /**
     * onExitGuide. Al pulsar el botón para salir de la guía.
     *
     * @param view
     */
    private void onExitGuide(View view) {
        //Guardamos needToStartGuide en sharedPreferences y lo cambiamos
        saveNeedToStartPreference(false);

        //Bloquearíamos el menú lateral. Como no hay pues no se bloquea

        //Volvemos a mostrar el constraintLayout
        binding.constraintLayout.setVisibility(View.VISIBLE);

        //Ocultamos la vista de la guía
        guideBinding.guideLayout.setVisibility(GONE);

        //Cerraríamos el menú lateral de existir


    }

    /**
     * Modificamos la preferencia needToStartPreference
     *
     * @param v
     */
    private void saveNeedToStartPreference(boolean v) {
        needToStartGuide = v;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("needToStartGuide", needToStartGuide);
        editor.apply();
    }

    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_characters)
            navController.navigate(R.id.navigation_characters);
        else if (menuItem.getItemId() == R.id.nav_worlds)
            navController.navigate(R.id.navigation_worlds);
        else
            navController.navigate(R.id.navigation_collectibles);
        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gestiona el clic en el ítem de información
        if (item.getItemId() == R.id.action_info) {
            showInfoDialog();  // Muestra el diálogo
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {
        // Crear un diálogo de información
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_about)
                .setMessage(R.string.text_about)
                .setPositiveButton(R.string.accept, null)
                .show();
    }


}