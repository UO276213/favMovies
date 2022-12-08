package es.uniovi.eii.sdm;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import es.uniovi.eii.sdm.modelo.Pelicula;

public class ListaPeliculasAdapter extends RecyclerView.Adapter<ListaPeliculasAdapter.PeliculaViewHolder> {


    // Interfaz para manejar el evento click sobre un elemento
    public interface OnItemClickListener {
        void onItemClick(Pelicula item);
    }

    private List<Pelicula> listaPeliculas;
    private final OnItemClickListener listener;

    public ListaPeliculasAdapter(List<Pelicula> listaPeli, OnItemClickListener listener) {
        this.listaPeliculas = listaPeli;
        this.listener = listener;
    }

    /* Indicamos el layout a "inflar" para usar en la vista
     */
    @NonNull
    @Override
    public PeliculaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos la vista con el layout para un elemento
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_recycler_view_pelicula, parent, false);
        return new PeliculaViewHolder(itemView);
    }


    /** Asocia el contenido a los componentes de la vista,
     * concretamente con nuestro PeliculaViewHolder que recibimos como parámetro
     */
    @Override
    public void onBindViewHolder(@NonNull PeliculaViewHolder holder, int position) {
        // Extrae de la lista el elemento indicado por posición
        Pelicula pelicula= listaPeliculas.get(position);
        Log.i("Lista","Visualiza elemento: "+pelicula);
        // llama al método de nuestro holder para asignar valores a los componentes
        // además, pasamos el listener del evento onClick
        holder.bindUser(pelicula, listener);
    }

    @Override
    public int getItemCount() {
        return listaPeliculas.size();
    }


   /*Clase interna que define los compoonentes de la vista*/

    public static class PeliculaViewHolder extends RecyclerView.ViewHolder{

        private TextView titulo;
        private TextView fecha;
        private ImageView imagen;

        public PeliculaViewHolder(View itemView) {
            super(itemView);

            titulo= (TextView)itemView.findViewById(R.id.titulopeli);
            fecha= (TextView)itemView.findViewById(R.id.fechaestreno);
            imagen= (ImageView)itemView.findViewById(R.id.imagen);
        }

        // asignar valores a los componentes
        public void bindUser(final Pelicula pelicula, final OnItemClickListener listener) {
            titulo.setText(pelicula.getTitulo()+" ("+pelicula.getFecha().substring(0,4)+")");
            fecha.setText(pelicula.getFecha());

//            fecha.setText(pelicula.getCategoria().getNombre());

            // cargar imagen desde la URL que nos proporcionan los datos
            // si la url es "" entonces ponemos la imagen por defecto
            if (pelicula.getUrlCaratula()=="") {
                imagen.setImageResource(R.drawable.pelicula_sin_imagen);
            } else {
                Picasso.get()
                        .load(pelicula.getUrlCaratula()).into(imagen);
            }

//            Picasso.get()
//                    .load(pelicula.getUrlCaratula()).into(imagen);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Log.i("ListaPeliculasAdapter", "Click");
                    listener.onItemClick(pelicula);
                }
            });
        }
    }

}
