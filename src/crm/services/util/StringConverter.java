package crm.services.util;

public class StringConverter {

	public static String convertStringToHTMLText(String plain){
		String result = "";
		String[] cad = plain.split(" ");
		for(int i=0; i<cad.length; i++){			
			String palabra = cad[i];
			String parte1 = null;
			int pos_a = cad[i].indexOf("á");
			int pos_e = cad[i].indexOf("é");
			int pos_i = cad[i].indexOf("í");
			int pos_o = cad[i].indexOf("ó");
			int pos_u = cad[i].indexOf("ú");
			int pos_A = cad[i].indexOf("Á");
			int pos_E = cad[i].indexOf("É");
			int pos_I = cad[i].indexOf("Í");
			int pos_O = cad[i].indexOf("Ó");
			int pos_U = cad[i].indexOf("Ú");
			int pos_ñ = cad[i].indexOf("ñ");
			int pos_Ñ = cad[i].indexOf("Ñ");
			int pos_die = cad[i].indexOf("ü");
			int pos_DIE = cad[i].indexOf("Ü");
			int pos_amp = cad[i].indexOf("&");
			int pos_apos = cad[i].indexOf("'");
			
			if(pos_a > 0){
				parte1 = cad[i].substring(0,pos_a);
				palabra = parte1.concat("&aacute");
				if(( cad[i].length() > (pos_a + 1)))
					palabra = palabra.concat(cad[i].substring(pos_a+1, cad[i].length()));			
			}
			else if(pos_e > 0){
				parte1 = cad[i].substring(0,pos_e);
				palabra = parte1.concat("&eacute");
				if(( cad[i].length() > (pos_e + 1)))
					palabra = palabra.concat(cad[i].substring(pos_e+1, cad[i].length()));			
			}
			else if(pos_i > 0){
				parte1 = cad[i].substring(0,pos_i);
				palabra = parte1.concat("&iacute");
				if(( cad[i].length() > (pos_i + 1)))
					palabra = palabra.concat(cad[i].substring(pos_i+1, cad[i].length()));			
			}
			else if(pos_o > 0){
				parte1 = cad[i].substring(0,pos_o);
				palabra = parte1.concat("&oacute");
				if(( cad[i].length() > (pos_o + 1)))
					palabra = palabra.concat(cad[i].substring(pos_o+1, cad[i].length()));			
			}
			else if(pos_u > 0){
				parte1 = cad[i].substring(0,pos_u);
				palabra = parte1.concat("&uacute");
				if(( cad[i].length() > (pos_u + 1)))
					palabra = palabra.concat(cad[i].substring(pos_u+1, cad[i].length()));			
			}
			
			else if(pos_A > 0){
				parte1 = cad[i].substring(0,pos_A);
				palabra = parte1.concat("&Aacute");
				if(( cad[i].length() > (pos_A + 1)))
					palabra = palabra.concat(cad[i].substring(pos_A+1, cad[i].length()));			
			}
			else if(pos_E > 0){
				parte1 = cad[i].substring(0,pos_E);
				palabra = parte1.concat("&Eacute");
				if(( cad[i].length() > (pos_E + 1)))
					palabra = palabra.concat(cad[i].substring(pos_E+1, cad[i].length()));			
			}
			else if(pos_I > 0){
				parte1 = cad[i].substring(0,pos_I);
				palabra = parte1.concat("&Iacute");
				if(( cad[i].length() > (pos_I + 1)))
					palabra = palabra.concat(cad[i].substring(pos_I+1, cad[i].length()));			
			}
			else if(pos_O > 0){
				parte1 = cad[i].substring(0,pos_O);
				palabra = parte1.concat("&Oacute");
				if(( cad[i].length() > (pos_O + 1)))
					palabra = palabra.concat(cad[i].substring(pos_O+1, cad[i].length()));			
			}
			else if(pos_U > 0){
				parte1 = cad[i].substring(0,pos_U);
				palabra = parte1.concat("&Uacute");
				if(( cad[i].length() > (pos_U + 1)))
					palabra = palabra.concat(cad[i].substring(pos_U+1, cad[i].length()));			
			}
			
			else if(pos_ñ > 0){
				parte1 = cad[i].substring(0,pos_ñ);
				palabra = parte1.concat("&ntilde");
				if(( cad[i].length() > (pos_ñ + 1)))
					palabra = palabra.concat(cad[i].substring(pos_ñ+1, cad[i].length()));			
			}
			else if(pos_Ñ > 0){
				parte1 = cad[i].substring(0,pos_Ñ);
				palabra = parte1.concat("&Ntilde");
				if(( cad[i].length() > (pos_Ñ + 1)))
					palabra = palabra.concat(cad[i].substring(pos_Ñ+1, cad[i].length()));			
			}
			
			else if(pos_die > 0){
				parte1 = cad[i].substring(0,pos_die);
				palabra = parte1.concat("&uuml");
				if(( cad[i].length() > (pos_die + 1)))
					palabra = palabra.concat(cad[i].substring(pos_die+1, cad[i].length()));			
			}
			else if(pos_DIE > 0){
				parte1 = cad[i].substring(0,pos_DIE);
				palabra = parte1.concat("&Uuml");
				if(( cad[i].length() > (pos_DIE + 1)))
					palabra = palabra.concat(cad[i].substring(pos_DIE+1, cad[i].length()));			
			}
			else if(pos_amp> 0){
				parte1 = cad[i].substring(0,pos_amp);
				palabra = parte1.concat("&amp");
				if(( cad[i].length() > (pos_amp + 1)))
					palabra = palabra.concat(cad[i].substring(pos_amp+1, cad[i].length()));			
			}
			else if(pos_apos>0){
				parte1 = cad[i].substring(0,pos_apos);
				palabra = parte1.concat(" ");
				if(( cad[i].length() > (pos_apos + 1)))
					palabra = palabra.concat(cad[i].substring(pos_apos+1, cad[i].length()));	
			}
			if(i==cad.length-1)
				result = result.concat(palabra);
			else result = result.concat(palabra+" ");
		}
		return result;
	}
	
}
