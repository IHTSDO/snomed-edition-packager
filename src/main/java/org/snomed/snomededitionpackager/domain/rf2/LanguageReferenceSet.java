package org.snomed.snomededitionpackager.domain.rf2;

import java.util.Objects;

public class LanguageReferenceSet {
	private String id;
	private String term;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LanguageReferenceSet that = (LanguageReferenceSet) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "LanguageReferenceSet{" +
				"id='" + id + '\'' +
				", term='" + term + '\'' +
				'}';
	}
}