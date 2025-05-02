package hrassessment.model;

import hrassessment.repository.CandidateDAO;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.FilterMeta;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LazyCandidateDataModel extends LazyDataModel<Candidate> {

    private final CandidateDAO candidateDAO;

    public LazyCandidateDataModel(CandidateDAO candidateDAO) {
        this.candidateDAO = candidateDAO;
    }

    @Override
    public List<Candidate> load(int first, int pageSize,
                                Map<String, SortMeta> sortBy,
                                Map<String, FilterMeta> filterBy) {
        List<Candidate> candidates = candidateDAO.getAllCandidates();

        if (filterBy != null && !filterBy.isEmpty()) {
            for (Map.Entry<String, FilterMeta> entry : filterBy.entrySet()) {
                String field = entry.getKey();
                String filterValue = String.valueOf(entry.getValue().getFilterValue()).toLowerCase();

                if (filterValue == null || filterValue.isBlank()) continue;

                candidates = candidates.stream()
                        .filter(candidate -> {
                            try {
                                switch (field) {
                                    case "name":
                                        return candidate.getName() != null &&
                                                candidate.getName().toLowerCase().contains(filterValue);
                                    case "email":
                                        return candidate.getEmail() != null &&
                                                candidate.getEmail().toLowerCase().contains(filterValue);
                                    case "birthDate":
                                        if (candidate.getBirthDate() == null) return false;

                                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                                        sdf.setTimeZone(java.util.TimeZone.getTimeZone("Europe/Moscow"));
                                        String dateStr = sdf.format(candidate.getBirthDate()).toLowerCase();
                                        return dateStr.contains(filterValue);

                                    default:
                                        return true;
                                }
                            } catch (Exception e) {
                                return true;
                            }
                        })
                        .collect(Collectors.toList());
            }
        }

        if (sortBy != null && !sortBy.isEmpty()) {
            SortMeta sortMeta = sortBy.values().iterator().next();
            String field = sortMeta.getField();
            boolean asc = sortMeta.getOrder().isAscending();

            Comparator<Candidate> comparator = switch (field) {
                case "name" -> Comparator.comparing(Candidate::getName, Comparator.nullsLast(String::compareToIgnoreCase));
                case "email" -> Comparator.comparing(Candidate::getEmail, Comparator.nullsLast(String::compareToIgnoreCase));
                case "birthDate" -> Comparator.comparing(Candidate::getBirthDate, Comparator.nullsLast(Comparator.naturalOrder()));
                default -> null;
            };

            if (comparator != null) {
                if (!asc) comparator = comparator.reversed();
                candidates.sort(comparator);
            }
        }

        setRowCount(candidates.size());
        int toIndex = Math.min(first + pageSize, candidates.size());
        if (first > toIndex) {
            return candidates;
        }
        return candidates.subList(first, toIndex);
    }

    @Override
    public String getRowKey(Candidate candidate) {
        return String.valueOf(candidate.getId());
    }

    @Override
    public Candidate getRowData(String rowKey) {
        int id = Integer.parseInt(rowKey);
        return candidateDAO.getCandidateById(id);
    }
}
