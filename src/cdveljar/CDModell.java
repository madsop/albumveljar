package cdveljar;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.AbstractListModel;

@SuppressWarnings("serial")
public class CDModell extends AbstractListModel{
	//private String dir;
	private String artist;
	private String album;
	private String[] songar;
	private PropertyChangeSupport pcs;
	//public final static String DIR_PROPERTY = "dir";
	public final static String ARTIST_PROPERTY = "artist";
	public final static String ALBUM_PROPERTY = "album";
	public final static String SONGAR_PROPERTY = "songar";
	
	public CDModell () {
		pcs = new PropertyChangeSupport(this);
		artist = "a"; album="b";
	}
	public CDModell (String artist, String album, String[] songar) {
		pcs = new PropertyChangeSupport(this);
		this.artist = artist;
		this.album = album;
		this.songar = songar;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
	
	public void setSongar(String[] songar) {
		String[] oldSongar = this.songar;
		this.songar = songar;
		pcs.firePropertyChange(SONGAR_PROPERTY, oldSongar, songar);
	}
	public String[] getSongar() {
		return songar;
	}
	
	public void setArtist (String artist) {
		String oldArtist = this.artist;
		this.artist = artist;
		pcs.firePropertyChange(ARTIST_PROPERTY, oldArtist, artist);
	}
	
	public void setAlbum (String album) {
		String oldAlbum = this.album;
		this.album = album;
		pcs.firePropertyChange(ALBUM_PROPERTY, oldAlbum, album);
	}
	
	public Object getElementAt(int arg0) {
		return songar[arg0];
	}
	public int getSize() {
		if (songar != null) {
			return songar.length;
		}
		else {
			return 0;
		}
	}
	public String getArtist() {
		return artist;
	}
	public String getAlbum() {
		return album;
	}
}