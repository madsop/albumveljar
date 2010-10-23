package cdveljar;

import javax.swing.JList;

public class Sang {
	protected static final String[] badends = {"prs","butt","cmg","gti","kzt","rep","ube","dcr","sns",
		"fnt","ser","wlm","mnd","rns","aaf","ss","pyt","iro","carstuar","evighet","led","gw",
		"ego","rbr","fjc","fff","mvp","xxl","dma","grux","mwf","vic","anti","ld","qtxmp3","dnr",
		"ksi"," simplemp3s","idm","ptsl","amrc","berc","nuhs","easymp3s","rh","esc","chr","chupa"},
		lydendingar = {"mp3","m4a","ogg","wma","flac","wav"};
	private String[] origfilnamn, songar;
	private int nullteljar = 0, antalsongar = 0;

	public Sang () {
		antalsongar = 0;
		nullteljar = 0;
	}

	/* 
	 * Get & set
	 */
	public int getNullteljar() {
		return nullteljar;
	}

	public int getAntalsongar() {
		return antalsongar;
	}

	public void setNullteljar(int nullteljar) {
		this.nullteljar = nullteljar;
	}

	public void setAntalsongar(int antalsongar) {
		this.antalsongar = antalsongar;
	}

	public String[] getOrigfilnamn() {
		return origfilnamn;
	}

	public String[] getSongar() {
		return songar;
	}

	public void setOrigfilnamn(String[] origfilnamn) {
		this.origfilnamn = origfilnamn;
	}

	public void setSongar(String[] songar) {
		this.songar = songar;
	}
	public void setSongarIndex(int i, String sang) {
		songar[i] = sang;
	}

	/* 
	 * Namnetriksing
	 */

	public void fikssanger() {
		origfilnamn = new String[songar.length];

		for (int i = 0; i < songar.length; i++) {
			origfilnamn[i] = songar[i];
		}
		fjernfilendingar();
		nullfjerning();
		antalsongar = songar.length - nullteljar;
		fiksendingar();
	}


	public String kommatriksing (String utvald) {
		if (utvald.contains(", ")) {
			int count = 0;
			for (int i = 0; i < utvald.length(); i++) {
				if (utvald.charAt(i) == ",".charAt(0)) {
					count++;
				}
			}
			if (count < 2) {
				String[] temp = utvald.split(", ");
				utvald = temp[1] + " " +temp[0];	
			}
		}
		return utvald;
	}

	public void fjernfilendingar() {
		if (songar == null) { }
		else {
			for (int i = 0; i < songar.length; i++) {
				if (songar[i] == null) {
					continue;
				}
				if (songar[i].contains("_")) {
					songar[i] = songar[i].replace("_", " ");
				}
				String b = songar[i].toLowerCase();
				boolean sjekka = false;
				for (int j = 0; j < lydendingar.length; j++) {
					if (b.endsWith("." +lydendingar[j])) {
						songar[i] = songar[i].substring(0, (songar[i].length() - (lydendingar[j].length()+1) ) );
						sjekka = true;
					}
				}
				if (sjekka == false) {
					songar[i] = null;
				}
			}
		}
	}

	public void nullfjerning () {
		if (songar == null) { }
		else {
			for (int i = 0; i < songar.length; i++) {
				for (int j = 0; j < songar.length; j++) {
					if (songar[i] == null) {
						if (i < j) {
							songar[i] = songar[j];
							songar[j] = null;
						}
						continue;
					}
					if (songar[j] == null) {
						if (j < i) {
							songar[j] = songar[i];
							songar[i] = null;
						}
						continue;
					}
					if ((songar[i].compareToIgnoreCase(songar[j]) > 0 && i <= j) || 
							(songar[j].compareToIgnoreCase(songar[i]) > 0 && j <= i) ) {
						String temp = songar[i];
						songar[i] = songar[j];
						songar[j] = temp;
						temp = origfilnamn[i];
						origfilnamn[i] = origfilnamn[j];
						origfilnamn[j] = temp;
					}
				}
				if (songar[i] == null) { nullteljar++; }
			}
		}
	}

	public void fiksendingar() {
		for (int i = 0; i < antalsongar; i++) {
			String b = songar[i].toLowerCase();		
			for (int j = 0; j < badends.length; j++) {
				if (b.endsWith("-" +badends[j])) {
					songar[i] = songar[i].substring(0, songar[i].length() - (badends[j].length()+1));
				}
			}
		}
	}

	public JList print(String[] songar, int antalsongar) {
		String[] sangor = new String[antalsongar];
		for (int i = 0; i < antalsongar; i++) {
			sangor[i] = songar[i];
		}
		return new JList(sangor);
	}
}