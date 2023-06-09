package com.saimone.pastebox.service;

import com.saimone.pastebox.api.request.PasteboxRequest;
import com.saimone.pastebox.api.response.PasteBoxUrlResponse;
import com.saimone.pastebox.api.response.PasteboxResponse;
import com.saimone.pastebox.model.PasteboxEntity;
import com.saimone.pastebox.repository.PasteboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Setter
public class PasteboxServiceImpl implements PasteboxService {

    private final PasteboxRepository repository;
    private AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public PasteboxResponse getByHash(String hash) {
        PasteboxEntity pasteboxEntity = repository.getByHash(hash);
        return new PasteboxResponse(pasteboxEntity.getData(), pasteboxEntity.getPasteboxStatus());
    }

    @Override
    public List<PasteboxResponse> getFirstPublicPasteboxes() {
        List<PasteboxEntity> list = repository.getListOfPublicAndAlive(10);

        return list.stream()
                .map(pasteboxEntity -> new PasteboxResponse(pasteboxEntity.getData(), pasteboxEntity.getPasteboxStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public PasteBoxUrlResponse create(PasteboxRequest request) {
        PasteboxEntity pasteboxEntity = new PasteboxEntity();
        long hash = generateId();
        pasteboxEntity.setId(hash);
        pasteboxEntity.setData(request.getData());
        pasteboxEntity.setHash(Long.toHexString(hash));
        pasteboxEntity.setPasteboxStatus(request.getPublicStatus());
        pasteboxEntity.setLifetime(LocalDateTime.now().plusSeconds(request.getExpirationTimeSeconds()));
        repository.add(pasteboxEntity);

        return new PasteBoxUrlResponse("/" + pasteboxEntity.getHash());
    }

    private long generateId() {
        return idGenerator.getAndIncrement();
    }
}
