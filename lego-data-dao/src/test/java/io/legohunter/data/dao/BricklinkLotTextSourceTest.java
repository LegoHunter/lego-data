package io.legohunter.data.dao;

import io.legohunter.data.dto.ExternalImageAlbum;
import io.legohunter.data.dto.MarketplaceListing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BricklinkLotTextSourceTest {
    private static final int FLICKR = 10;

    @Mock
    private ExternalImageAlbumDao externalImageAlbumDao;
    @Mock
    private ItemInventoryPhotoDao itemInventoryPhotoDao;

    private BricklinkLotTextSource source;

    @BeforeEach
    void setUp() {
        source = new BricklinkLotTextSource(externalImageAlbumDao, itemInventoryPhotoDao);
    }

    @Test
    void composesFromTheListingTheFlickrAlbumAndTheCaptions() {
        when(externalImageAlbumDao.findByExternalServiceIdAndItemInventoryId(FLICKR, 958))
                .thenReturn(Optional.of(album("https://bit.ly/x", "https://www.flickr.com/photos/u/sets/1/")));
        when(itemInventoryPhotoDao.findProcessedCaptionsByItemInventoryId(958)).thenReturn(List.of("Small mark."));

        assertThat(source.description(listing(null, "Test Car"), true).text()).isEqualTo(
                "Test Car. <a href=\"https://bit.ly/x\">View all photos</a>. "
                        + "See Extended Description for additional Item Condition notes.");
        assertThat(source.extendedDescription(958)).isEqualTo("Small mark.");
    }

    @Test
    void noAlbumAndNoCaptionLeaveOnlyTheText() {
        when(externalImageAlbumDao.findByExternalServiceIdAndItemInventoryId(FLICKR, 958)).thenReturn(Optional.empty());
        when(itemInventoryPhotoDao.findProcessedCaptionsByItemInventoryId(958)).thenReturn(List.of());

        assertThat(source.description(listing("Box has shelf wear", "Test Car"), true).text()).isEqualTo("Box has shelf wear");
        assertThat(source.extendedDescription(958)).isEmpty();
    }

    @Test
    void anAlbumWithoutAnAddressYetHasNoLink() {
        when(externalImageAlbumDao.findByExternalServiceIdAndItemInventoryId(FLICKR, 958)).thenReturn(Optional.of(album(null, null)));
        assertThat(source.albumLink(958)).isNull();
    }

    @Test
    void aListingWithNothingToShowReadsNothing() {
        assertThat(source.description(listing(" ", null), true).present()).isFalse();
        assertThat(source.description(null, true).present()).isFalse();
        assertThat(source.extendedDescription(null)).isEmpty();
        verifyNoInteractions(externalImageAlbumDao);
        verifyNoInteractions(itemInventoryPhotoDao);
    }

    private static MarketplaceListing listing(String description, String title) {
        MarketplaceListing listing = new MarketplaceListing();
        listing.setItemInventoryId(958);
        listing.setDescription(description);
        listing.setTitle(title);
        return listing;
    }

    private static ExternalImageAlbum album(String shortUrl, String albumUrl) {
        return ExternalImageAlbum.builder().shortUrl(shortUrl).albumUrl(albumUrl).build();
    }
}
