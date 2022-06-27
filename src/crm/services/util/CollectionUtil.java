package crm.services.util;

import java.rmi.RemoteException;
import java.util.List;

import crm.libraries.abm.helper.SalaHelper;
import crm.libraries.abm.helper.ServicioHelper;

public class CollectionUtil {

    public static Object[] listToObjectArray(List list) {
		
		Object[] results = new Object[list.size()];
		for (int i=0; i< results.length;i++){ 
		    if(list.get(i) instanceof Object[]){
		    	results[i] = (Object[]) list.get(i);	
		    }else{
		    	results[i] = (Object) list.get(i);
		    }
		}
		
		return results;
    }
    
    public static ServicioHelper[] orderServiciosArray(ServicioHelper[] array){
    	int i, j;
    	ServicioHelper temp;

        for (i=1; i<array.length; i++)
             for (j=0; j<array.length - i; j++)
                  if (((ServicioHelper)array[j]).getOrden() > ((ServicioHelper)array[j+1]).getOrden())
                       {
                       /* Intercambiamos */
                       temp = array[j];
                       array[j] = array[j+1];
                       array[j+1] = temp;
                       }
        return array;
    }
    
    public static SalaHelper[] orderSalasArray(SalaHelper[] array){
    	int i, j;
    	SalaHelper temp;

        for (i=1; i<array.length; i++)
             for (j=0; j<array.length - i; j++)
                  if (((SalaHelper)array[j]).getOrden() > ((SalaHelper)array[j+1]).getOrden())
                       {
                       /* Intercambiamos */
                       temp = array[j];
                       array[j] = array[j+1];
                       array[j+1] = temp;
                       }
        return array;
    }
	
	/*

    public static Object[] listToObject(List list) {
		
		Object[] results = new Object[list.size()];
		for (int i=0; i< results.length;i++){ 
		    results[i] = (Object) list.get(i);
		}
		
		return results;
    }
    */
    /*
    public static String[] listToObjectArray(List list) {
		
    	String[] results = new String[list.size()];
		for (int i=0; i< results.length;i++){ 
		    results[i] = (String[]) list.get(i);
		}
		
		return results;
    }
    */
}
