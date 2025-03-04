package dam.pmdm.spyrothedragon;

import static android.view.View.GONE;
import static android.view.View.inflate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;
import dam.pmdm.spyrothedragon.databinding.GuideBinding;
import dam.pmdm.spyrothedragon.databinding.GuideEndBinding;
import dam.pmdm.spyrothedragon.databinding.GuideMainBinding;

public class MainActivity extends AppCompatActivity {

    private int step = 1;
    private int WIDTH;
    private int HEIGHT;
    private Float DESPLAZAMIENTO;
    private ActivityMainBinding binding;
    private GuideBinding guideBinding;
    private GuideMainBinding guideMainBinding;
    private GuideEndBinding guideEndBinding ;
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
        guideEndBinding = binding.includeEndGuideLayout;

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


        //Calculamos medidas de la pantall
        calcularMedidas();

        //Cargamos las preferencias; importante para needToStartGuide
        cargarPreferencias();
        //Inicializamos la guía si needToStartGuide está a true
        //PARA AHORRAR TIEMPO ponemos el needToStartGuide a true siempre
        //needToStartGuide = true;

        //Iniciamos la página principal de la guía, tiene un botón que lleva a la guía normal
        if (needToStartGuide) {
            initializeMainGuide();
        }
        //initializeGuide();
    }

    @Override
    protected void onStart() {

        if (getIntent().getExtras() != null) {
            String fragment = getIntent().getExtras().getString("fragment");
            if (fragment != null) {
                if (fragment.equals("collectibles")) {
                    binding.navView.setSelectedItemId(R.id.nav_collectibles);
                }
            }
        }
        super.onStart();
    }

    /**
     * Calcula medidas de la pantalla para mover cosas
     * Use WindowMetrics instead.
     * Obtain a WindowMetrics instance by calling WindowManager. getCurrentWindowMetrics(),
     * then call WindowMetrics. getBounds() to get the dimensions of the application window.
     */
    private void calcularMedidas() {
        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        WIDTH = size.x;
        HEIGHT = size.y;
        //int height = size.y
        DESPLAZAMIENTO = Float.parseFloat(String.valueOf(WIDTH)) / 3;
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
        //Reproducimos un sonido y esperamos
        reproducir(R.raw.release, true);

        //Si tenemos algún menú lateral, hay que bloquearlo

        //Ocultamos las vistas principal y guía paginada
        binding.constraintLayout.setVisibility(View.GONE);
        guideBinding.guideLayout.setVisibility(View.GONE);
        //Mostramos la vista de la mainGuide
        guideMainBinding.guideMain.setVisibility(View.VISIBLE);

    }

    /**
     * Inicializamos la guía en sí. Cada click en la capa cambiará de página
     */
    private void initializeGuide(View view) {
        //onClickListener del botón exitGuide
        guideBinding.exitguide.setOnClickListener(this::onExitGuide);
        //onClickListener de la guía
        guideBinding.guideLayout.setOnClickListener(this::onNextPage);
        //onClickListener del botón siguiente
        guideBinding.nextPage.setOnClickListener(this::onNextPage);

        //Si tenemos algún menú lateral, hay que bloquearlo

        //Ocultamos la guía main
        guideMainBinding.guideMain.setVisibility(View.GONE);

        //Animación
        parpadear();
        //Ponemos la guía a visible
        binding.constraintLayout.setVisibility(View.VISIBLE);
        guideBinding.guideLayout.setVisibility(View.VISIBLE);
        guideBinding.textStep.setVisibility(View.VISIBLE);

    }

    /**
     * Parpadeo del circulito
     */
    private void parpadear() {
        guideBinding.textStep.setVisibility(View.VISIBLE);

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
        animatorSet.setDuration(500);
        animatorSet.start();


        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                //if (needToStartGuide) {
                    super.onAnimationEnd(animation);
                    //Abrimos el siguiente binding
                    //guideBinding.pulseImage.setVisibility(View.GONE);
                    //guideBinding.textStep.setText(R.string.guide_page1);
                    guideBinding.textStep.setVisibility(View.VISIBLE);
                    //guideBinding.exitguide.setVisibility(View.VISIBLE);
                //}
            }
        });


    }

    /**
     * Cada vez que se pulsa la guía, avanza una página
     *
     * @param view
     */
    private void onNextPage(View view) {

        //animation = new ObjectAnimator();
        //Cambiamos el botón de sitio
        guideBinding.pulseImage.setVisibility(View.VISIBLE);
        switch (step) {
            case 0:
                binding.navView.setSelectedItemId(R.id.nav_characters);
                break;
            case 1:
                guideBinding.textStep.setVisibility(View.GONE);
                ObjectAnimator animation = ObjectAnimator.ofFloat(guideBinding.pulseImage, "translationX", DESPLAZAMIENTO);
                animation.setDuration(2000);
                animation.start();
                parpadear();
                reproducir(R.raw.gem, false);
                guideBinding.textStep.setText(R.string.guide_page2);
                guideBinding.textStep.setVisibility(View.VISIBLE);
                savePageCompleted(1);
                binding.navView.setSelectedItemId(R.id.nav_worlds);
                break;
            case 2:
                guideBinding.textStep.setVisibility(View.GONE);
                animation = ObjectAnimator.ofFloat(guideBinding.pulseImage, "translationX", DESPLAZAMIENTO * 2);
                animation.setDuration(2000);
                animation.start();
                parpadear();
                reproducir(R.raw.spyro_sign);
                guideBinding.textStep.setText(R.string.guide_page3);
                guideBinding.textStep.setVisibility(View.VISIBLE);
                savePageCompleted(2);
                binding.navView.setSelectedItemId(R.id.nav_collectibles);
                break;
            case 3:
                guideBinding.textStep.setVisibility(View.GONE);
                ObjectAnimator ejeX = ObjectAnimator.ofFloat(guideBinding.pulseImage, "translationX", DESPLAZAMIENTO * 2);
                ObjectAnimator ejeY = ObjectAnimator.ofFloat(guideBinding.pulseImage, "translationY", -(HEIGHT * 0.85f));
                //guideBinding.pulseImage.setBackgroundColor(R.color.yellow);
                //guideBinding.pulseImage.setColorFilter(R.color.yellow);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(ejeY).with(ejeX);
                animatorSet.setDuration(2000);
                animatorSet.start();
                parpadear();
                reproducir(R.raw.wizards);
                guideBinding.textStep.setText(R.string.guide_page4);
                guideBinding.textStep.setVisibility(View.VISIBLE);
                savePageCompleted(3);
                break;
            case 4:
                guideBinding.textStep.setVisibility(View.GONE);
                binding.includeLayout.guideLayout.setVisibility(View.GONE);
                //mostrarLecciones();
                break;
            case 5:
                guideBinding.textStep.setVisibility(View.GONE);
                binding.constraintLayout.setVisibility(View.GONE);
                binding.includeEndGuideLayout.guideEndLayout.setVisibility(View.GONE);
                //Salimos de la guía
                reproducir(R.raw.one_up, true);
                //Mostramos en el texto las partes completadas de la guía
                //guideBinding.textStep.setText(R.string.guide_page4);
                //guideBinding.textStep.setVisibility(View.VISIBLE);
                savePageCompleted(4);
                onExitGuide(view);
                break;

        }
        //Incrementamos el step
        step++;
        //Desplazamos el frame


    }

    /**
     * Al salir de la guía se resaltan las lecciones aprendidas, poniendo el alpha a 1.
     */
    private void mostrarLecciones() {
        binding.constraintLayout.setVisibility(View.GONE);
        binding.navHostFragment.setVisibility(View.GONE);
        binding.includeEndGuideLayout.guideEndLayout.setVisibility(View.VISIBLE);
        guideEndBinding.guideEndLayout.setOnClickListener(this::hideEndGuide);
        guideEndBinding.btnHideEndGuide.setOnClickListener(this::hideEndGuide);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Recuperamos las lecciones guardadas (por defecto a false)
        boolean page1 = sharedPreferences.getBoolean("page1", false);
        boolean page2 = sharedPreferences.getBoolean("page2", false);
        boolean page3 = sharedPreferences.getBoolean("page3", false);
        boolean page4 = sharedPreferences.getBoolean("page4", false);
        //Vamos cambiando el alpha/style de cada textview
        if (page1)
            guideEndBinding.charactersCompleted.setAlpha(1);
        if (page2)
            guideEndBinding.worldsCompleted.setAlpha(1);
        if (page3)
            guideEndBinding.collectiblesCompleted.setAlpha(1);
        if (page4)
            guideEndBinding.aboutCompleted.setAlpha(1);

    }

    /**
     * Oculta la última página de la guía
     * @param view
     */
    private void hideEndGuide(View view) {
        binding.includeEndGuideLayout.guideEndLayout.setVisibility(GONE);
        binding.navHostFragment.setVisibility(View.VISIBLE);
    }

    /**
     * Reproduce un audio pasado como R.raw.<<ID>>
     *
     * @param audioId ID del Audio en RAW
     * @param esperar Si está a TRUE, esperamos a que termine de reproducirse
     */
    private void reproducir(int audioId, boolean esperar) {

        MediaPlayer mep = MediaPlayer.create(this, audioId);
        mep.start();
        if (esperar) {
            /*
            try {
                wait(mep.getDuration());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            */

        }
    }

    /**
     * Reproduce un audio pasado como R.raw.<ID></ID>
     *
     * @param audioId ID del Audio en RAW
     */
    private void reproducir(int audioId) {

        MediaPlayer mep = MediaPlayer.create(this, audioId);
        mep.start();

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
        //mostrarLecciones();
        //Volvemos a mostrar el constraintLayout
        binding.constraintLayout.setVisibility(View.VISIBLE);

        //Ocultamos la vista de la guía
        guideBinding.guideLayout.setVisibility(GONE);
        //binding.includeEndGuideLayout.guideEndLayout.setVisibility(View.VISIBLE);

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

    /**
     * Cuando completamos una página, la guardamos en sharedPreferences
     */
    private void savePageCompleted(int pagina) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String preference = "page" + pagina;
        editor.putBoolean(preference, true);
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