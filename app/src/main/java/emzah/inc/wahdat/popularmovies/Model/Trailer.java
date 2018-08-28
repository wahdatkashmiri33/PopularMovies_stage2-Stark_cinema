
package emzah.inc.wahdat.popularmovies.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Trailer {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<ResultTrailer> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<ResultTrailer> getResults() {
        return results;
    }

    public void setResults(List<ResultTrailer> results) {
        this.results = results;
    }

}
