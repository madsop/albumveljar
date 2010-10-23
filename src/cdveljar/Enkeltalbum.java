package cdveljar;

import java.io.File;
import java.util.Random;

public class Enkeltalbum {
	private String dir,artist,album;
	private int spelarno;
	private Sang sanger;
	private Main main;
	protected boolean id3tag, tilfeldigsang;
	private CDModell cdmodell;

	public Enkeltalbum(Main main) {
		this.main = main;
		cdmodell = new CDModell();
	}



	public Sang getSanger() {
		return sanger;
	}
	public void setSanger(Sang sanger) {
		this.sanger = sanger;
	}

	public CDModell getCdmodell() {
		return cdmodell;
	}
	public void setCdmodell(CDModell cdmodell) {
		this.cdmodell = cdmodell;
	}

	public int getSpelarno() {
		return spelarno;
	}
	public void aukSpelarno() {
		spelarno++;
	}
	public void senkSpelarno() {
		spelarno--;
	}
	public void setSpelarno(int no) {
		spelarno = no;
	}
	public void nullstillSpelarno() {
		spelarno=0;
	}
	
	public String getDir() {
		return dir;
	}



	public void getAlbumTags() {
		for (int i = 0; i < sanger.getSongar().length; i++) {
			try {
				main.getPlayer().getControl().open(new File(dir + sanger.getOrigfilnamn()[i]));
				sanger.setSongarIndex(i,i+1 + " - " +main.getPlayer().getPlayer().m_audioFileFormat.properties().get("title"));
			}
			catch (BasicPlayerException bpe) {
				break;
			}
		}
		cdmodell.setSongar(sanger.getSongar());
	}



	/*
	 * Sett opp songane
	 */
	public boolean filnamnOppsett() {
		try {
			String[] filer = Fillister.dirlist(dir, true);
			sanger.setSongar(filer);
			sanger.setOrigfilnamn(filer);

			sanger.fikssanger();

			if (sanger.getAntalsongar()<=0) {
				// viss undermapper: utforsk. elles
				try {
					sanger.setNullteljar(0);
					String[] temp = Fillister.folderlist(dir);
					Random t = new Random();
					int valdplate = t.nextInt(temp.length-1);
					dir = temp[valdplate];
					sanger.setSongar(Fillister.dirlist(dir, true));
					sanger.setOrigfilnamn(sanger.getSongar());
					sanger.fikssanger();
					cdmodell.setSongar(sanger.getSongar());
					spelarno=0;
					
					String[] split = dir.split("/");
					album = album + " - " +split[split.length-1];
					System.out.println(album);
					cdmodell.setAlbum(album);
					
					main.spelSang();
				}
				catch (Exception e) {
					System.out.println("Funka dårleg." +dir);
					return false;
				}
			}
			else { 
				// Fjern rippegruppe-namn
				sanger.fiksendingar();
				cdmodell.setSongar(sanger.getSongar());
				spelarno=0;
				main.spelSang();
			}
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean fix(String dir, String artist, String album) {
		this.dir = dir;
		this.artist = artist;
		this.album = album;

		sanger = new Sang();
		spelarno = 0;

		// Tweak printout
		// utvald[0] = mappe, utvald[1] = artist, utvald[2] = album
		this.dir = "" +dir +"/" +this.artist +"/" +this.album +"/";

		this.artist = sanger.kommatriksing(this.artist);
		/*if (utvald[1].length() > 40) {
			cdmodell.setArtist(utvald[1].substring(0,40));
		}
		else {
			cdmodell.setArtist(utvald[1]);
		}
		if (utvald[2].length() > 40) {
			cdmodell.setAlbum(utvald[2].substring(0,40));
		}
		else {
			cdmodell.setAlbum(utvald[2]);
		}*/
		cdmodell.setArtist(this.artist);
		cdmodell.setAlbum(this.album);

		// All song-triksing går herfrå
		boolean b = filnamnOppsett();
		if (id3tag){
			getAlbumTags();
			spelarno = 0;
			main.spelSang();
		}
		if (b) {
			return true;
		}
		else {
			return false;
		}
	}
}