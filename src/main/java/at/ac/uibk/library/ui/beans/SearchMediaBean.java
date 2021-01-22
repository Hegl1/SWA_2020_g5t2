package at.ac.uibk.library.ui.beans;

import at.ac.uibk.library.model.Media;
import at.ac.uibk.library.model.MediaType;
import at.ac.uibk.library.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

@Component
@Scope("view")
public class SearchMediaBean implements Serializable {
    private Collection<Media> initResults;
    private Collection<Media> results;

    private String search = null;
    private String filterType = null;
    private String filterLanguage = null;
    private String filterAvailable = null;

    /**
     * Returns the list of filtered results
     *
     * @return the collection of filtered medias
     */
    public Collection<Media> getResults() {
        return results;
    }

    @Autowired
    private MediaService mediaService;

    /**
     * Initializes the results with all media from the database
     * (if not yet initialized)
     */
    private void init() {
        if(this.initResults == null){
            setInit(mediaService.getAllMedia());
        }
    }

    /**
     * Initializes the results with the given collection
     *
     * @param initResults the collection to initialize
     */
    public void setInit(Collection<Media> initResults) {
        if(initResults != null){
            this.initResults = initResults;

            doFilter();
        }else{
            if(this.initResults != null) return;
            init();
        }
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(final String search) {
        this.search = search == null ? null : search.trim();

        if(search != null && this.search.isEmpty()){
            this.search = null;
        }
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(final String filterType) {
        this.filterType = filterType == null ? null : filterType.trim().toUpperCase();

        if(filterType != null && this.filterType.isEmpty()){
            this.filterType = null;
        }
    }

    public String getFilterLanguage() {
        return filterLanguage;
    }

    public void setFilterLanguage(final String filterLanguage) {
        this.filterLanguage = filterLanguage == null ? null : filterLanguage.trim();

        if(filterLanguage != null && this.filterLanguage.isEmpty()){
            this.filterLanguage = null;
        }
    }

    public String getFilterAvailable() {
        return filterAvailable;
    }

    public void setFilterAvailable(final String filterAvailable) {
        this.filterAvailable = filterAvailable == null ? null : filterAvailable.trim();

        if(filterAvailable != null && this.filterAvailable.isEmpty()){
            this.filterAvailable = null;
        }
    }

    private MediaType getFilterMediaType(){
        if(filterType == null) return null;

        switch (filterType){
            case "BOOK": return MediaType.BOOK;
            case "AUDIOBOOK": return MediaType.AUDIOBOOK;
            case "VIDEO": return MediaType.VIDEO;
            case "MAGAZINE": return MediaType.MAGAZINE;
            default: return null;
        }
    }

    /**
     * Filteres the collection by the set filters and sets the results collection
     */
    public void doFilter(){
        Collection<Media> results = new ArrayList<>(initResults);

        if(search != null){
            results = mediaService.filterMediaByTitle(results, search);
        }

        MediaType filterType = getFilterMediaType();

        if(filterType != null){
            results = mediaService.filterMediaByType(results, filterType);
        }

        if(filterLanguage != null){
            results = mediaService.filterMediaByLanguage(results, filterLanguage);
        }

        if(filterAvailable != null){
            results = mediaService.filterMediaByAvailability(results, filterAvailable.equalsIgnoreCase("YES"));
        }

        this.results = results;
    }

    /**
     * Resets the filter and result
     */
    public void doResetFilter(){
        this.search = null;
        this.filterType = null;
        this.filterLanguage = null;
        this.filterAvailable = null;

        this.results = new ArrayList<>(initResults);
    }
}