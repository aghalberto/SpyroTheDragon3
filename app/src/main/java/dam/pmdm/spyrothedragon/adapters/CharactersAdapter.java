package dam.pmdm.spyrothedragon.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import dam.pmdm.spyrothedragon.CanvasActivity;
import dam.pmdm.spyrothedragon.MultimediaActivity;
import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Character;

import java.util.List;

public class CharactersAdapter extends RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder> {

    private List<Character> list;

    public CharactersAdapter(List<Character> charactersList) {
        this.list = charactersList;
    }

    @Override
    public CharactersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CharactersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CharactersViewHolder holder, int position) {

        Character character = list.get(position);
        holder.nameTextView.setText(character.getName());

        //Listener longkeypressed
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                Character c = list.get(holder.getAdapterPosition());
                if (c.getName().equals("Spyro")){
                    easterEggSpyro(v);
                }
                return false;
            }
        });

        // Cargar la imagen (simulado con un recurso drawable)
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(character.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);
    }

    /**
     * Easter Egg Spyro. Nos llevamos la vista.
     * @param view
     */
    public void easterEggSpyro(View view){
        Intent intent = new Intent (view.getContext(), CanvasActivity.class);
        view.getContext().startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CharactersViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;

        public CharactersViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }
}
