package es.uniovi.eii.sdm.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import es.uniovi.eii.sdm.R;
import es.uniovi.eii.sdm.modelo.Actor;

public class ListaActoresAdapter extends RecyclerView.Adapter<ListaActoresAdapter.ActorViewHolder> {


    // Interfaz para manejar el evento click sobre un elemento
    public interface OnItemClickListener {
        void onItemClick(Actor item);
    }

    private List<Actor> listaActores;
    private final OnItemClickListener listener;

    public ListaActoresAdapter(List<Actor> listaActores, OnItemClickListener listener) {
        this.listaActores = listaActores;
        this.listener = listener;
    }

    /* Indicamos el layout a "inflar" para usar en la vista
     */
    @NonNull
    @Override
    public ActorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos la vista con el layout para un elemento
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_recycler_view_actor, parent, false);
        return new ActorViewHolder(itemView);
    }

    /** Asocia el contenido a los componentes de la vista,
     * concretamente con nuestro ActorViewHolder que recibimos como parámetro
     */
    @Override
    public void onBindViewHolder(@NonNull ActorViewHolder holder, int position) {
        // Extrae de la lista el elemento indicado por posición
        Actor actor= listaActores.get(position);
        Log.i("Lista","Visualiza elemento: "+actor);
        // llama al método de nuestro holder para asignar valores a los componentes
        // además, pasamos el listener del evento onClick
        holder.bindUser(actor, listener);
    }

    @Override
    public int getItemCount() {
        return listaActores.size();
    }


    /*Clase interna que define los compoonentes de la vista*/

    public static class ActorViewHolder extends RecyclerView.ViewHolder{

        private TextView nombre_actor;
        private TextView nombre_personaje;
        private ImageView imagen_actor;

        public ActorViewHolder(View itemView) {
            super(itemView);

            nombre_actor= (TextView)itemView.findViewById(R.id.nombre_actor);
            nombre_personaje= (TextView)itemView.findViewById(R.id.nombre_personaje);
            imagen_actor= (ImageView)itemView.findViewById(R.id.imagen_actor);
        }

        // asignar valores a los componentes
        public void bindUser(final Actor actor, final OnItemClickListener listener) {
            nombre_actor.setText(actor.getNombre_actor());
            nombre_personaje.setText(actor.getNombre_personaje());
            //fecha.setText(actor.getFecha());

          //  nombre_personaje.setText(actor.getNombre_personaje());

            // cargar imagen desde la URL que nos proporcionan los datos

            //la url se construye con:
            //https://image.tmdb.org/t/p/{tam_imagen}/{contenido_campo_imagen}
            //Donde tam_imagen, define el tamaño en pixels de la imagen entre varios predefinidos: "w92","w154","w185","w342","w500","w780","original"
            if (actor.getImagen()=="") {
                imagen_actor.setImageResource(R.drawable.actor_sin_imagen);
            } else {
                Picasso.get()
                        .load(actor.getImagen()).into(imagen_actor);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("ListaActoresAdapter", "Click");
                    listener.onItemClick(actor);
                }
            });
        }
    }

}
