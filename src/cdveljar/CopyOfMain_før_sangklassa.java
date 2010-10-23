package cdveljar;

/*
 * Todo:
 * 
 * 	Bugfiksing
 * 		- For lange namn
 * 		- Automatisk neste sang / ikkje run-kall heile tida
 * 		- Ikkje alle album får songane vist
 * 		- Blir alle album med? - viss ikkje, korfor ikkje?
 * 
 * 	Nye funksjonar
 *		- Framsyning av kva for sang som spelast av
 *		- Mulegheit til å velje album frå ei dropdown-liste
 *		- Mulegheit til å velje førre plate
 *		- Syning av id3-taggar?
 *
 *	Elegance is not optional
 *		- Kommentering
 *		- Grafisk pirk
 *		- Gå skikkeleg over koden
 *		- Kva som skrives ut
 */

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Random;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class CopyOfMain_før_sangklassa extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private ArrayList<String> album;
	private CDModell cdmodell;
	private int spelarno;
	private String[] albumString, dirs,artist, songar, origfilnamn, utvald;
	private String dir;
	private BasicPlayerTest player;
	private int nullteljar = 0, antalsongar = 0;
	private static final int breidde = 700, hogde = 300, fontstr = 15;
	private static final String tomMelding = "D'oh, her var det inga musikk-mapper. Prøv igjen?";
	private static final String[] badends = {"prs","butt","cmg","gti","kzt","rep","ube","dcr","sns",
		"fnt","ser","wlm","mnd","rns","aaf","ss","pyt","iro","carstuar","evighet","led","gw",
		"ego","rbr","fjc","fff","mvp","xxl","dma","grux","mwf","vic","anti","ld","qtxmp3","dnr",
		"ksi"," simplemp3s","idm","ptsl","amrc","berc","nuhs","easymp3s","rh","esc","chr","chupa"},
		lydendingar = {"mp3","m4a","ogg","wma","flac"};

	// GUI
	private GridBagLayout gbl;
	private JLabel artistlabel, albumlabel;
	private GridBagConstraints c;
	private JButton velny, spel,stopp,pause,forrige,neste;
	private JList jlist;
	private JFileChooser jfc;

	/*
	 * Startar avspelinga av ein sang
	 */
	public void spelSang() {
		//if (player.getPlayer().getMStatus() != BasicPlayer.PLAYING) {
		String tobeplayed = utvald[0] + origfilnamn[spelarno]; 
		if (tobeplayed.endsWith(".mp3") || tobeplayed.endsWith(".flac")) {
			player.play(tobeplayed);
		}
		//	}
	}

	/*
	 * Spelar neste sang - viss det er fleire songar på albumet
	 */
	public void nestesang() {
		spelarno++;
		if (spelarno < origfilnamn.length) {
			spelSang();
		}
		else {
			JOptionPane.showMessageDialog(this, "Plata er ferdig");
		}
	}

	private void fix () {
		album = new ArrayList<String>();
		antalsongar = 0;
		nullteljar = 0;
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
			int vald = r.nextInt(albumString.length-1);

			// Tweak printout
			utvald = albumString[vald].split(" --- ");			
			// utvald[0] = mappe, utvald[1] = artist, utvald[2] = album
			utvald[0] = "" +utvald[0] +"/" +utvald[1] +"/" +utvald[2] +"/";

			utvald[1] = kommatriksing(utvald[1]);
			cdmodell.setArtist(utvald[1]);
			cdmodell.setAlbum(utvald[2]);

			// All song-triksing går herfrå

			try {
				songar = Fillister.dirlist(utvald[0], true);

				origfilnamn = new String[songar.length];

				for (int i = 0; i < songar.length; i++) {
					origfilnamn[i] = songar[i];
				}

				// Fjern filendingar, og sett til null viss ikkje fila er eit lydspor
				songar = fjernfilendingar(songar);

				// fjern nullane-metoden kalles her
				songar = nullfjerning(songar);

				antalsongar = songar.length-nullteljar;

				if (antalsongar<=0) {
					// viss undermapper: utforsk. elles
					try {
						nullteljar = 0;
						String[] temp = Fillister.folderlist(utvald[0]);
						Random t = new Random();
						int valdplate = t.nextInt(temp.length-1);
						String dir = temp[valdplate];
						songar = Fillister.dirlist(dir, true);
						songar = fjernfilendingar(songar);
						songar = nullfjerning(songar);
						antalsongar = songar.length-nullteljar;
						songar = fiksendingar (songar,antalsongar);
						cdmodell.setSongar(songar);
						origfilnamn = new String[songar.length];

						for (int i = 0; i < songar.length; i++) {
							origfilnamn[i] = songar[i];
						}
					}
					catch (Exception e) {
						System.out.println("Funka dårleg." +utvald[0]);
						fix();
					}
				}
				else { 
					// Fjern rippegruppe-namn
					songar = fiksendingar(songar, antalsongar);
					cdmodell.setSongar(songar);
					spelarno=0;
					spelSang();
				}
			}
			catch (Exception e) {
				fix();
			}
		}
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

	public String[] fjernfilendingar(String[] songar) {
		if (songar == null) { return null;}
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
		return songar;
	}

	public String[] nullfjerning (String[] songar) {
		if (songar == null) { return null;}
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
		return songar;
	}

	public String[] fiksendingar(String[] songar, int antalsongar) {
		for (int i = 0; i < antalsongar; i++) {
			String b = songar[i].toLowerCase();		
			for (int j = 0; j < badends.length; j++) {
				if (b.endsWith("-" +badends[j])) {
					songar[i] = songar[i].substring(0, songar[i].length() - (badends[j].length()+1));
				}
			}
		}
		return songar;
	}

	public JList print(String[] songar, int antalsongar) {
		String[] sangor = new String[antalsongar];
		for (int i = 0; i < antalsongar; i++) {
			sangor[i] = songar[i];
		}
		return new JList(sangor);
	}

	public CopyOfMain_før_sangklassa(String[] args) {
		cdmodell = new CDModell();
		cdmodell.addPropertyChangeListener(this);
		jlist = new JList(cdmodell);
		gbl = new GridBagLayout();
		player = new BasicPlayerTest();
		setLayout(gbl);
		c = new GridBagConstraints();
		c.ipadx = 40;
		c.ipady = 10;
		c.anchor = GridBagConstraints.WEST;

		if (args.length == 0) {
			jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			jfc.setMultiSelectionEnabled(true);
			JOptionPane.showMessageDialog(this,"Velkommen. Dette programmet vel eit tilfeldig album for deg. Du skjønner nok koss det verkar, først no bør du velje mappa der du har musikken din","Tilfeldig-album-veljar",1);
			int returnVal = jfc.showOpenDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String[] temp = new String[jfc.getSelectedFiles().length];
				for (int dir = 0; dir<temp.length; dir++) {
					temp[dir] = jfc.getSelectedFiles()[dir].toString();
				}
				dirs = temp;
				fix();
			}
			else if (returnVal == JFileChooser.CANCEL_OPTION) {
				System.exit(0);
			}
			else {
				JOptionPane.showMessageDialog(null, tomMelding);
				System.exit(0);
			}
		}
		else {
			dirs = args;
			fix();
		}


		artistlabel = new JLabel(cdmodell.getArtist());
		artistlabel.setFont(new Font("Monotype Corsiva", 1, fontstr));
		artistlabel.setAutoscrolls(true);
		c.gridx = 0;
		c.gridy = 1;
		add(artistlabel,c);

		albumlabel = new JLabel(cdmodell.getAlbum());
		//albumtittel.setPreferredSize(new Dimension(350,45));
		albumlabel.setFont(new Font("Monotype Corsiva", 1, fontstr));
		c.gridx = 0;
		c.gridy = 0;
		add(albumlabel,c);

		JScrollPane sp = new JScrollPane();
		sp.setPreferredSize(new Dimension(breidde, hogde));
		sp.getViewport().add(jlist);

		Dimension d = new Dimension(30,10);

		velny = new JButton();
		velny.setText("Ny!");
		velny.addActionListener(new velnycomp(this));
		velny.setPreferredSize(d);

		spel = new JButton("Spel");
		spel.addActionListener(new velnycomp(this));
		spel.setPreferredSize(d);
		stopp = new JButton("Stopp");
		stopp.addActionListener(new velnycomp(this));
		stopp.setPreferredSize(d);
		pause = new JButton("Pause");
		pause.addActionListener(new velnycomp(this));
		pause.setPreferredSize(d);
		forrige = new JButton("Førre");
		forrige.addActionListener(new velnycomp(this));
		forrige.setPreferredSize(d);
		neste = new JButton("Neste");
		neste.addActionListener(new velnycomp(this));
		neste.setPreferredSize(d);

		c.gridheight = 6;
		c.gridwidth = 9;
		c.gridx = 0;
		c.gridy = 3;
		add(sp,c);

		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		add(velny,c);

		c.gridx = 1;
		add(forrige,c);
		c.gridx = 2;
		add(spel,c);
		c.gridx = 3;
		add(pause,c);
		c.gridx = 4;
		add(stopp,c);
		c.gridx = 5;
		add(neste,c);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Tilfeldig vald album vart");
		frame.setContentPane(new CopyOfMain_før_sangklassa(args));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	class velnycomp implements ActionListener {
		CopyOfMain_før_sangklassa main;
		public velnycomp(CopyOfMain_før_sangklassa main) {
			this.main = main;
		}
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == velny) {
				cdmodell = new CDModell();
				cdmodell.addPropertyChangeListener(main);
				jlist.setModel(cdmodell);
				try {
					System.out.println("PLAYER: " +player.getControl());
					player.getControl().stop();
				}
				catch (BasicPlayerException e) {
					System.exit(0);
				}
				finally {
					fix();
					artistlabel.setText(cdmodell.getArtist());
					albumlabel.setText(cdmodell.getAlbum());
				}
			}
			else if (arg0.getSource()==spel) {
				if (player.getControl() == null) { spelSang(); }
				if (player.getControl().getMStatus() == BasicPlayer.PAUSED) {
					try {
						player.getControl().resume();
					}
					catch (BasicPlayerException bpe) {

					}
				}	
				else /*if (player.getControl().getMStatus() != BasicPlayer.PLAYING)*/ {
					spelSang();
				}
			}
			else if (arg0.getSource()==pause) {
				try {
					player.getControl().pause();
				}
				catch (Exception e) {

				}
			}
			else if (arg0.getSource()==stopp) {
				try {
					player.getControl().stop();
				}
				catch (BasicPlayerException bpe) {
				}
			}
			else if (arg0.getSource()==neste) {
				try {
					player.getControl().stop();
					nestesang();
				}
				catch (BasicPlayerException bpe) {}
			}
			else if (arg0.getSource()==forrige) {
				if (spelarno >= 1) {
					try {
						player.getControl().stop();
						spelSang();
					}
					catch (BasicPlayerException bpe) {}
					finally {
						spelarno--;
					}
				}
			}
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == CDModell.ARTIST_PROPERTY) {
			artistlabel.setText(cdmodell.getArtist());
		}
		if (evt.getSource() == CDModell.ALBUM_PROPERTY) {
			albumlabel.setText(cdmodell.getAlbum());
		}
	}
}