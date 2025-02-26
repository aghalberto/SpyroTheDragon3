package dam.pmdm.spyrothedragon.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.spyrothedragon.MainActivity;
import dam.pmdm.spyrothedragon.MultimediaActivity;
import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Collectible;

public class CollectiblesAdapter extends RecyclerView.Adapter<CollectiblesAdapter.CollectiblesViewHolder> {

    /**
     * Pulsaciones consecutivas
     */
    private int pulsaciones = 0;
    private List<Collectible> list;

    public CollectiblesAdapter(List<Collectible> collectibleList) {
        this.list = collectibleList;
    }

    @Override
    public CollectiblesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CollectiblesViewHolder(view);
    }

    @Override
    //public void onBindViewHolder(CollectiblesViewHolder holder, int position) {
    public void onBindViewHolder(CollectiblesViewHolder holder, int position) {
        //Añadimos un click

        holder.itemView.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                Collectible c = list.get(holder.getAdapterPosition());
                if (c.getName().equals("Gemas")){
                    pulsaciones++;
                } else {
                    pulsaciones = 0;
                }
                if (pulsaciones == 4) easterEggGemas(v);
            }
        });

        Collectible collectible = list.get(position);
        holder.nameTextView.setText(collectible.getName());

        // Cargar la imagen (simulado con un recurso drawable)
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(collectible.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);
    }

    /**
     * Invocación del EasterEgg gemas
     */
    private void easterEggGemas(View view) {
       //Toast.makeText(view.getContext(), "Hola", Toast.LENGTH_LONG);
        Intent intent = new Intent (view.getContext(), MultimediaActivity.class);
        view.getContext().startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CollectiblesViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;

        public CollectiblesViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }
}
