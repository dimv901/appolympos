package fpuna.py.com.appolympos.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import fpuna.py.com.appolympos.R;
import fpuna.py.com.appolympos.activities.LocateActivity;
import fpuna.py.com.appolympos.activities.NoSaleActivity;
import fpuna.py.com.appolympos.activities.PictureActivity;
import fpuna.py.com.appolympos.activities.RouteActivity;
import fpuna.py.com.appolympos.activities.TakeOrderActivity;
import fpuna.py.com.appolympos.entities.Clientes;
import fpuna.py.com.appolympos.repository.ClienteRepository;
import fpuna.py.com.appolympos.utiles.Utils;

/**
 * Created by Diego on 6/12/2017.
 */

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ClienteViewHolder> implements Filterable {

    private List<Clientes> mClientList = new ArrayList<>();
    private Context mContext;
    private ClienteFilter clientsFilter;
    public static final String CLIENT_ID = "CLIENT_ID";

    public ClientsAdapter(List<Clientes> clientsList, Context context) {
        mClientList = clientsList;
        mContext = context;
    }


    public class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView direccion;
        TextView ruc;
        TextView hora;
        FloatingActionButton actionMap;
        FloatingActionButton actionLocate;
        FloatingActionButton actionNoSale;
        FloatingActionButton actionSale;
        FloatingActionButton actionCall;
        FloatingActionButton actionPicture;
        FloatingActionButton actionView;
        ImageView picture;


        public ClienteViewHolder(View view) {
            super(view);
            nombre = (TextView) view.findViewById(R.id.item_client_name);
            direccion = (TextView) view.findViewById(R.id.item_client_address);
            ruc = (TextView) view.findViewById(R.id.item_client_ruc);
            picture = (ImageView) view.findViewById(R.id.item_client_image);
            hora = (TextView) view.findViewById(R.id.item_client_hora_visita);

            actionSale = (FloatingActionButton) view.findViewById(R.id.item_client_action_sale);
            actionNoSale = (FloatingActionButton) view.findViewById(R.id.item_client_action_no_sale);
            actionView = (FloatingActionButton) view.findViewById(R.id.item_client_info);
            actionMap = (FloatingActionButton) view.findViewById(R.id.item_client_place);
            actionPicture = (FloatingActionButton) view.findViewById(R.id.item_client_shot);
            actionCall = (FloatingActionButton) view.findViewById(R.id.item_client_call);
            actionLocate = (FloatingActionButton) view.findViewById(R.id.item_client_locate);

            //SALE
            actionSale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, TakeOrderActivity.class);
                    intent.putExtra(CLIENT_ID, getItemAtPosition(getAdapterPosition()).getId());
                    mContext.startActivity(intent);
                }
            });

            //NO SALE
            actionNoSale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NoSaleActivity.class);
                    intent.putExtra(CLIENT_ID, getItemAtPosition(getAdapterPosition()).getId());
                    mContext.startActivity(intent);
                }
            });

            //VIEW
            actionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInfoCliente(getItemAtPosition(getAdapterPosition()));
                }
            });

            //MAP
            actionMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Clientes c = getItemAtPosition(getAdapterPosition());
                    if (c.getGeolocalizado()) {
                        Intent intent = new Intent(mContext, RouteActivity.class);
                        intent.putExtra(CLIENT_ID, getItemAtPosition(getAdapterPosition()).getId());
                        mContext.startActivity(intent);
                    } else {
                        Utils.getToast(mContext, "El cliente aun no ha sido geolocalizado.");
                    }

                }
            });

            //GPS
            actionLocate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Clientes c = getItemAtPosition(getAdapterPosition());
                    if (c.getGeolocalizado()) {
                        showDialogGeolocation(c);
                    } else {
                        Intent intent = new Intent(mContext, LocateActivity.class);
                        intent.putExtra(CLIENT_ID, getItemAtPosition(getAdapterPosition()).getId());
                        mContext.startActivity(intent);
                    }
                }
            });

            //PICTURE
            actionPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Clientes c = getItemAtPosition(getAdapterPosition());
                    if (c.getTieneFoto()) {
                        showDialogPicture(c);
                    } else {
                        Intent intent = new Intent(mContext, PictureActivity.class);
                        intent.putExtra(CLIENT_ID, getItemAtPosition(getAdapterPosition()).getId());
                        mContext.startActivity(intent);
                    }
                }
            });

            actionCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogCall(getItemAtPosition(getAdapterPosition()));
                }
            });
        }
    }

    private void showDialogGeolocation(final Clientes c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.dialog_client_already_locate).setTitle(R.string.dialog_info_title);
        builder.setIcon(R.mipmap.ic_info_black_24dp);
        builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mContext, LocateActivity.class);
                intent.putExtra(CLIENT_ID, c.getId());
                mContext.startActivity(intent);
            }
        });

        builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showDialogPicture(final Clientes c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.dialog_client_already_picture).setTitle(R.string.dialog_info_title);
        builder.setIcon(R.mipmap.ic_info_black_24dp);
        builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mContext, PictureActivity.class);
                intent.putExtra(CLIENT_ID, c.getId());
                mContext.startActivity(intent);
            }
        });

        builder.setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }


    private void showDialogCall(final Clientes c) {
        final String[] phoneNumbers = new String[2];

        if (c.getTelefonoNegocio() != null) {
            phoneNumbers[0] = c.getTelefonoNegocio();
        }

        if (c.getContactoTitular() != null) {
            phoneNumbers[1] = c.getContactoTitular();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.label_make_call)
                .setItems(phoneNumbers, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                makeCall(phoneNumbers[0]);
                                break;
                            case 1:
                                makeCall(phoneNumbers[1]);
                                break;
                        }

                    }
                }).setNegativeButton(R.string.label_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void makeCall(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        mContext.startActivity(intent);
    }

    @Override
    public ClientsAdapter.ClienteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client, parent, false);
        return new ClienteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ClientsAdapter.ClienteViewHolder holder, int position) {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        //System.out.println("HORA DATE " + mClientList.get(position).getHoraVisita());
        sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date sDate = null;
        try {
            sDate = sdf.parse(mClientList.get(position).getHoraVisita());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf = new SimpleDateFormat("HH:mm");
        String sData = sdf.format(sDate);
        if (mClientList.get(position).getVisitado()) {
            holder.hora.setText("VISITADO");
            holder.hora.setTextColor(mContext.getResources().getColor(R.color.colorSuccess));
        } else {
            holder.hora.setText("VISITAR: " + sData + " Hs.");
            holder.hora.setTextColor(mContext.getResources().getColor(R.color.colorError));
        }
        holder.nombre.setText(mClientList.get(position).getNombreNegocio());
        holder.direccion.setText(mClientList.get(position).getDireccion());
        holder.ruc.setText(mClientList.get(position).getRuc());
        if (mClientList.get(position).getTieneFoto()) {
            try {
                byte[] imgbytes = Base64.decode(mClientList.get(position).getFoto(), Base64.DEFAULT);
                holder.picture.setImageBitmap(BitmapFactory.decodeByteArray(imgbytes, 0, imgbytes.length));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public int getItemCount() {
        return mClientList.size();
    }

    @Override
    public Filter getFilter() {
        if (clientsFilter == null)
            clientsFilter = new ClienteFilter();
        return clientsFilter;
    }

    public Clientes getItemAtPosition(int position) {
        return mClientList.get(position);
    }

    public void setData(List<Clientes> data) {
        mClientList = new ArrayList<>();

        Comparator<Clientes> mComparator = new Comparator<Clientes>() {
            @Override
            public int compare(Clientes o1, Clientes o2) {
                Date date1 = null;
                Date date2 = null;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                try {
                    date1 = sdf.parse(o1.getHoraVisita());
                    date2 = sdf.parse(o2.getHoraVisita());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //return (date1.getTime() > date1.getTime() ? -1 : 1);     //descending
                return (date1.getTime() > date2.getTime() ? 1 : -1);     //ascending
            }
        };


        mClientList.addAll(data);
        Collections.sort(mClientList, mComparator);
        notifyDataSetChanged();
    }

    private void showInfoCliente(Clientes cliente) {
        StringBuilder sb = new StringBuilder();
        sb.append("DESCRIPCION: ")
                .append(cliente.getNombreNegocio())
                .append("\n")
                .append("R.U.C: ")
                .append(cliente.getRuc())
                .append("\n")
                .append("DIRECCION: ")
                .append(cliente.getDireccion())
                .append("\n");

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setIcon(R.mipmap.ic_details_black_24dp);
        builder.setTitle(R.string.dialog_info_title);
        builder.setMessage(sb.toString());
        builder.setPositiveButton(R.string.label_accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Dialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }


    public class ClienteFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toUpperCase();

            FilterResults results = new FilterResults();
            final List<Clientes> list = ClienteRepository.getAll();

            int count = list.size();
            final ArrayList<Clientes> nlist = new ArrayList<Clientes>(count);

            String filterableString = constraint.toString().toUpperCase();

            for (Clientes clients : list) {
                if (clients.getNombreNegocio().toUpperCase().contains(filterString) || clients.getDireccion().toUpperCase().contains(filterableString) | clients.getRuc().toUpperCase().contains(filterableString)) {
                    nlist.add(clients);
                }
            }

            Comparator<Clientes> mComparator = new Comparator<Clientes>() {
                @Override
                public int compare(Clientes o1, Clientes o2) {
                    Date date1 = null;
                    Date date2 = null;
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    try {
                        date1 = sdf.parse(o1.getHoraVisita());
                        date2 = sdf.parse(o2.getHoraVisita());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //return (date1.getTime() > date1.getTime() ? -1 : 1);     //descending
                    return (date1.getTime() > date2.getTime() ? 1 : -1);     //ascending
                }
            };

            Collections.sort(nlist, mComparator);
            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mClientList = (List<Clientes>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}


