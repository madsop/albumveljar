package cdveljar;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListDataListener;

public class Album implements ComboBoxModel{

	private ArrayList<String> album;
	private String[] albumString, dirs,artist,utvald;
	private String dir;
	private Main main;
	private Sang sanger;
	private int valdNr;
	private int spelarno;
	protected boolean id3tag;
	private CDModell cdmodell;
	protected static final String tomMelding = "D'oh, her var det inga musikk-mapper. Prøv igjen?";

	/*
	 * Get & set
	 */
	public ArrayList<String> getAlbum() {
		return album;
	}
	public void setAlbum(ArrayList<String> album) {
		this.album = album;
	}

	public Sang getSanger() {
		return sanger;
	}
	public void setSanger(Sang sanger) {
		this.sanger = sanger;
	}

	public String[] getDirs() {
		return dirs;
	}
	public void setDirs(String[] dirs) {
		this.dirs = dirs;
	}

	public CDModell getCdmodell() {
		return cdmodell;
	}
	public void setCdmodell(CDModell cdmodell) {
		this.cdmodell = cdmodell;
	}


	public String[] getUtvald() {
		return utvald;
	}
	public void setUtvald(String[] utvald) {
		this.utvald = utvald;
	}

	public int getSpelarno() {
		return spelarno;
	}
	public void setSpelarno(int spelarno) {
		this.spelarno = spelarno;
	}

	/*
	 * Konstruktør
	 */
	public Album(Main main) {
		this.main = main;
		cdmodell = new CDModell();
		valdNr = 0;
		utvald = new String[0]; 
		album = new ArrayList<String>();
	}

	/*
	 * Startar ei ny plate
	 */
	protected void fix () {
		main.refreshJList();
		spelarno = 0;
		album = new ArrayList<String>();
		sanger = new Sang();
		for (int i = 0; i < dirs.length; i++) {
			dir = dirs[i];
			try {
				artist = Fillister.dirlist(dir, false);
			}
			catch (Exception e) {
			}

			// for kvar artist
			for (int artistnr = 0; artistnr < artist.length; artistnr++) {
				String dirArtist = dir +"/" +artist[artistnr];
				try {
					String[] filerArtist = Fillister.dirlist(dirArtist, false);
					for (int albumnr = 0; albumnr<filerArtist.length; albumnr++) {
						album.add(dir + " --- " +artist[artistnr] +" --- " +filerArtist[albumnr]);
					}
				}
				catch (Exception e) {
					continue;
				}
			}
		}
		albumString = new String[album.size()];

		for (int i = 0; i < album.size(); i++) {
			albumString[i] = album.get(i);
		}

		if (albumString.length < 1) {
			System.out.println(tomMelding);
			JOptionPane.showMessageDialog(null, tomMelding);
			System.exit(0);
		}
		else {	
			Random r = new Random();
			valdNr = r.nextInt(albumString.length-1);

			// Tweak printout
			utvald = albumString[valdNr].split(" --- ");			
			// utvald[0] = mappe, utvald[1] = artist, utvald[2] = album
			utvald[0] = "" +utvald[0] +"/" +utvald[1] +"/" +utvald[2] +"/";

			utvald[1] = sanger.kommatriksing(utvald[1]);
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
			cdmodell.setArtist(utvald[1]);
			cdmodell.setAlbum(utvald[2]);

			// All song-triksing går herfrå
			filnamnOppsett();
		}
		if (id3tag){
			getAlbumTags();
			spelarno = 0;
			main.spelSang();
		}
	}

	public void getAlbumTags() {
		for (int i = 0; i < sanger.getSongar().length; i++) {
			try {
				main.getPlayer().getControl().open(new File(utvald[0] + sanger.getOrigfilnamn()[i]));
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
	public void filnamnOppsett() {
		try {
			String[] filer = Fillister.dirlist(utvald[0], true);
			sanger.setSongar(filer);
			sanger.setOrigfilnamn(filer);

			sanger.fikssanger();

			if (sanger.getAntalsongar()<=0) {
				// viss undermapper: utforsk. elles
				try {
					sanger.setNullteljar(0);
					String[] temp = Fillister.folderlist(utvald[0]);
					Random t = new Random();
					int valdplate = t.nextInt(temp.length-1);
					String dir = temp[valdplate];
					sanger.setSongar(Fillister.dirlist(dir, true));
					sanger.fikssanger();
					cdmodell.setSongar(sanger.getSongar());
					sanger.setOrigfilnamn(sanger.getSongar());
				}
				catch (Exception e) {
					System.out.println("Funka dårleg." +utvald[0]);
					fix();
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
			fix();
		}
	}

	/*
	 * Spelar neste sang - viss det er fleire songar på albumet
	 */
	public void nestesang() {
		spelarno++;
		if (spelarno < sanger.getOrigfilnamn().length) {
			main.spelSang();
		}
		else {
			JOptionPane.showMessageDialog(main, "Plata er ferdig");
			fix();
		}
	}
	public Object getSelectedItem() {
		if (utvald.length > valdNr) {
			return utvald[valdNr];
		}
		else {
			return null;
		}
	}
	public void setSelectedItem(Object anItem) {
		if (album.contains(anItem)) {
			valdNr = album.indexOf(anItem);
		}		
	}
	public void addListDataListener(ListDataListener arg0) {
		// TODO Auto-generated method stub
	}
	public Object getElementAt(int arg0) {
		// TODO Auto-generated method stub
		if (utvald.length > arg0) {
			return utvald[arg0];
		}
		else {
			return null;
		}
	}
	public int getSize() {
		return album.size();
	}
	public void removeListDataListener(ListDataListener arg0) {
		// TODO Auto-generated method stub	
	}

}
