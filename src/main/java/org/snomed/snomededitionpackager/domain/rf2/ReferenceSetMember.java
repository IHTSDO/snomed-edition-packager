package org.snomed.snomededitionpackager.domain.rf2;

public class ReferenceSetMember {
	private String[] fieldNames;
	private String id;
	private String effectiveTime;
	private String active;
	private String moduleId;
	private String refsetId;
	private String referencedComponentId;
	private String[] otherValues;

	public String[] getFieldNames() {
		return fieldNames;
	}

	public ReferenceSetMember setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
		return this;
	}

	public String getId() {
		return id;
	}

	public ReferenceSetMember setId(String id) {
		this.id = id;
		return this;
	}

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public ReferenceSetMember setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
		return this;
	}

	public String getActive() {
		return active;
	}

	public ReferenceSetMember setActive(String active) {
		this.active = active;
		return this;
	}

	public String getModuleId() {
		return moduleId;
	}

	public ReferenceSetMember setModuleId(String moduleId) {
		this.moduleId = moduleId;
		return this;
	}

	public String getRefsetId() {
		return refsetId;
	}

	public ReferenceSetMember setRefsetId(String refsetId) {
		this.refsetId = refsetId;
		return this;
	}

	public String getReferencedComponentId() {
		return referencedComponentId;
	}

	public ReferenceSetMember setReferencedComponentId(String referencedComponentId) {
		this.referencedComponentId = referencedComponentId;
		return this;
	}

	public String[] getOtherValues() {
		return otherValues;
	}

	public ReferenceSetMember setOtherValues(String[] otherValues) {
		this.otherValues = otherValues;
		return this;
	}

	public String toRF2() {
		StringBuilder sb = new StringBuilder();
		sb.append(id).append('\t')
				.append(effectiveTime).append('\t')
				.append(active).append('\t')
				.append(moduleId).append('\t')
				.append(refsetId).append('\t')
				.append(referencedComponentId);

		if (otherValues != null) {
			for (String otherValue : otherValues) {
				sb.append('\t').append(otherValue);
			}
		}

		return sb.toString();
	}

}