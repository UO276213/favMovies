package es.uniovi.eii.favmovies;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import es.uniovi.eii.favmovies.modelos.Pelicula;

public class ListaPeliculasAdapter extends RecyclerView.Adapter<ListaPeliculasAdapter.PeliculaViewHolder> {

    private final OnItemClickListener listener;
    private List<Pelicula> filmsList;
    public ListaPeliculasAdapter(List<Pelicula> filmsList, OnItemClickListener listener) {
        this.filmsList = filmsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ListaPeliculasAdapter.PeliculaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.linea_recicler_view_pelicula, parent, false);
        return new PeliculaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaPeliculasAdapter.PeliculaViewHolder holder, int position) {
        Pelicula film = filmsList.get(position);

        Log.i("Lista", "Visualiza elemento: " + film);

        holder.bindUser(film, listener);

    }

    @Override
    public int getItemCount() {
        return filmsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Pelicula film);
    }

    protected class PeliculaViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView date;
        private ImageView image;

        public PeliculaViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titulopeli);
            date = itemView.findViewById(R.id.fechaestreno);
            image = itemView.findViewById(R.id.imagen);
        }

        public void bindUser(final Pelicula film, final OnItemClickListener listener) {
//            title.setText(film.getTitle() + " " + film.getDate());
            title.setText(film.getTitle());
            date.setText(film.getDate());

//            date.setText(film.getCategory().getNombre());

            //cargar Imagen
            Picasso.get().load(film.getCoverUrl()).into(image);

            itemView.setOnClickListener(view -> listener.onItemClick(film));
        }
    }
}
