package de.samply.share.client.quality.report.dktk;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Slot;
import de.samply.share.common.utils.MdrIdDatatype;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DktkIdMdrIdConverterImpl implements DktkIdMdrIdConverter {

  private static final String DKTK_ID = "DKTK_ID";
  private static final String ADT_ID = "ADT_ID";

  private final MdrClient mdrClient;
  private final Map<MdrIdDatatype, String> dktkIds = new HashMap<>();


  public DktkIdMdrIdConverterImpl(MdrClient mdrClient) {
    this.mdrClient = mdrClient;
  }

  @Override
  public String getDktkId(MdrIdDatatype mdrId) {
    String dktkId = dktkIds.get(mdrId);
    if (dktkId == null) {

      dktkId = requestDktkId(mdrId);

      if (dktkId == null) {
        dktkId = mdrId.getNamespace() + "-" + mdrId.getId();
      }
      dktkIds.put(mdrId, dktkId);

    }
    return dktkId;
  }

  private String getDktkId(List<Slot> slots) {
    if (slots != null) {
      for (Slot slot : slots) {

        if (isSlotOfType(slot, DKTK_ID) || isSlotOfType(slot, ADT_ID)) {
          return slot.getSlotValue();
        }

      }
    }
    return null;
  }

  private String requestDktkId(MdrIdDatatype mdrId) {
    List<Slot> slots = getSlots(mdrId);
    return getDktkId(slots);

  }

  private boolean isSlotOfType(Slot slot, String slotType) {
    return slot.getSlotName() != null && slot.getSlotName().replaceAll("\\s+", "").equals(slotType);
  }

  private List<Slot> getSlots(MdrIdDatatype mdrId) {
    try {
      return mdrClient.getDataElementSlots(mdrId.toString());
    } catch (MdrConnectionException | ExecutionException | MdrInvalidResponseException e) {
      return null;
    }
  }

}
