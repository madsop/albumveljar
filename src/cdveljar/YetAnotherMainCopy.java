package cdveljar;

/*
 * Todo:
 * 
 * 	Bugfiksing
 * 		- Ikkje alle album får songane vist
 * 		- Blir alle album med? - viss ikkje, korfor ikkje?
 * 		- Tags for FLAC
 * 
 * 	Nye funksjonar
 *		- Mulegheit til å velje album frå ei dropdown-liste
 *		- Mulegheit til å velje førre plate
 *		- Vel sang ved å dobbeltklikke / skrive inn nummer
 *		- Volumjustering
 *
 *	Elegance is not optional
 *		- Kommentering
 *		- Grafisk pirk
 *		- Gå skikkeleg over koden
 *		- Kva som skrives ut
 */

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class YetAnotherMainCopy extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;
	private BasicPlayerTest player;
	private static final int breidde = 700, hogde = 300, fontstr = 15;
	private Album album;

	// GUI
	private GridBagLayout gbl;
	private JLabel artistlabel, albumlabel;
	private GridBagConstraints c;
	private JButton velny, spel,stopp,pause,forrige,neste;
	private JList jlist;
	private JFileChooser jfc;
	private JCheckBox id3tagbox;
	private JComboBox albumliste;

	/*
	 * Startar avspelinga av ein sang
	 */
	public void spelSang() {
		//if (player.getPlayer().getMStatus() != BasicPlayer.PLAYING) {
		int spelarno = album.getSpelarno();
		String tobeplayed = album.getUtvald()[0] + album.getSanger().getOrigfilnamn()[spelarno]; 
		if (tobeplayed.endsWith(".mp3") || tobeplayed.endsWith(".flac")) {
			player.play(tobeplayed);
			jlist.setSelectedIndex(spelarno);
		}
	/*	if (tobeplayed.endsWith(".flac")) {
			player.play(tobeplayed);
			jlist.setSelectedIndex(spelarno);
		}
		else {
			fix();
			spelSang();
		}*/
		//	}
	}
	

	public BasicPlayerTest getPlayer() {
		return player;
	}

	public void refreshJList() {
		CDModell cdmodell = album.getCdmodell();
		album.setCdmodell(new CDModell(cdmodell.getArtist(),cdmodell.getAlbum(),cdmodell.getSongar()));
		album.getCdmodell().addPropertyChangeListener(this);
		jlist.setModel(album.getCdmodell());		
	}

	public YetAnotherMainCopy(String[] args) {
		album = new Album((Main) this);
		album.getCdmodell().addPropertyChangeListener(this);
		jlist = new JList(album.getCdmodell());
		gbl = new GridBagLayout();
		player = new BasicPlayerTest();
		setLayout(gbl);
		c = new GridBagConstraints();
		c.ipadx = 40;
		c.ipady = 10;
		c.anchor = GridBagConstraints.WEST;

		settOppGui();

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
				album.setDirs(temp);
				album.fix();
			}
			else if (returnVal == JFileChooser.CANCEL_OPTION) {
				System.exit(0);
			}
			else {
				JOptionPane.showMessageDialog(null, Album.tomMelding);
				System.exit(0);
			}
		}
		else {
			album.setDirs(args);
			album.fix();
		}
	}

	public void settOppGui() {

		albumliste = new JComboBox(album);
		
		artistlabel = new JLabel(album.getCdmodell().getArtist());
		artistlabel.setFont(new Font("Monotype Corsiva", 1, fontstr));
		artistlabel.setAutoscrolls(true);
		c.gridwidth = 9;
		c.gridx = 0;
		c.gridy = 1;
		add(artistlabel,c);

		albumlabel = new JLabel(album.getCdmodell().getAlbum());
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
		spel.setEnabled(false);
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

		id3tagbox = new JCheckBox("Bruk tags?",album.id3tag);
		id3tagbox.addActionListener(new velnycomp(this));

		c.gridheight = 6;
		c.gridwidth = 9;
		c.gridx = 0;
		c.gridy = 4;
		add(sp,c);
		
		c.gridy = 5;
		c.gridheight = 1;
		add(albumliste,c);

		c.gridheight = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		c.gridy = 2;
		add(velny,c);
		c.gridx = 1;
		add(id3tagbox,c);

		c.gridx = 2;
		add(forrige,c);
		c.gridx = 3;
		add(spel,c);
		c.gridx = 4;
		add(pause,c);
		c.gridx = 5;
		add(stopp,c);
		c.gridx = 6;
		add(neste,c);
		
		player.addPropertyChangeListener(this);
		
	}
	
	public void setStatusForPlayingButtons (boolean verdi) {
		spel.setEnabled(verdi);
		pause.setEnabled(verdi);
		stopp.setEnabled(verdi);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Tilfeldig vald album vart");
		frame.setContentPane(new YetAnotherMainCopy(args));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	class velnycomp implements ActionListener {
		YetAnotherMainCopy main;

		public velnycomp(YetAnotherMainCopy main) {
			this.main = main;
		}
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == velny) {
				album.setCdmodell(new CDModell());
				album.getCdmodell().addPropertyChangeListener(main);
				jlist.setModel(album.getCdmodell());
				try {
					System.out.println("PLAYER: " +player.getControl());
					player.getControl().stop();
				}
				catch (BasicPlayerException e) {
					System.exit(0);
				}
				finally {
					album.fix();
					artistlabel.setText(album.getCdmodell().getArtist());
					albumlabel.setText(album.getCdmodell().getAlbum());
					spel.setEnabled(false);
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
				else {
					spelSang();
				}
				pause.setEnabled(true);
				stopp.setEnabled(true);
				spel.setEnabled(false);
			}
			else if (arg0.getSource()==pause) {
				try {
					player.getControl().pause();
					setStatusForPlayingButtons(true);
					pause.setEnabled(false);
				}
				catch (Exception e) {

				}
			}
			else if (arg0.getSource()==stopp) {
				try {
					player.getControl().stop();
					setStatusForPlayingButtons(false);
					spel.setEnabled(true);
				}
				catch (BasicPlayerException bpe) {
				}
			}
			else if (arg0.getSource()==neste) {
				try {
					player.getControl().stop();
					setStatusForPlayingButtons(true);
					spel.setEnabled(false);
					album.nestesang();
				}
				catch (BasicPlayerException bpe) {}
			}
			else if (arg0.getSource()==forrige) {
				if (album.getSpelarno() >= 1) {
					try {
						player.getControl().stop();
						spelSang();
					}
					catch (BasicPlayerException bpe) {}
					finally {
						album.setSpelarno(album.getSpelarno()-1);
						setStatusForPlayingButtons(true);
						spel.setEnabled(false);
					}
				}
			}
			else if (arg0.getSource()==id3tagbox) {
				if (album.id3tag) {
					album.id3tag = false;
					album.filnamnOppsett();
					refreshJList();
				}
				else {
					album.id3tag = true;
					album.getAlbumTags();
					album.getCdmodell().setSongar(album.getSanger().getSongar());
					refreshJList();
					album.setSpelarno(0);
					spelSang();
				}
			}
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == CDModell.ARTIST_PROPERTY) {
			artistlabel.setText(album.getCdmodell().getArtist());
		}
		if (evt.getPropertyName() == CDModell.ALBUM_PROPERTY) {
			albumlabel.setText(album.getCdmodell().getAlbum());
		}
		if (evt.getPropertyName() == BasicPlayerTest.EOM_PROPERTY) {
			album.nestesang();
		}
	}
}