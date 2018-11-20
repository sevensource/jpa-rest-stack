/*
 * Copyright 2014-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sevensource.support.rest.etag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.Test;
import org.sevensource.support.rest.model.SimpleTestEntity;
import org.springframework.data.annotation.Version;
import org.springframework.http.HttpHeaders;


public class ETagTest {
	
	@Test // DATAREST-160
	public void createsETagFromVersionValue() throws Exception {
		SimpleTestEntity entity = new SimpleTestEntity() {
			private Integer version = 1;
			@Override
			public Integer getVersion() {
				return version;
			}
		};
		ETag from = ETag.from(entity);

		assertThat(from.toString()).isEqualTo("\"1\"");
	}

	@Test // DATAREST-160
	public void surroundsValueWithQuotationMarksOnToString() {
		assertThat(ETag.from("1").toString()).isEqualTo("\"1\"");
	}

	@Test // DATAREST-160
	public void returnsNoEtagForNullStringSource() {
		assertThat(ETag.from((String) null)).isEqualTo(ETag.NO_ETAG);
	}

	@Test // DATAREST-160
	public void returnsNoEtagForNullPersistentEntityResourceSource() {

		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
			ETag.from((SimpleTestEntity) null);
		});
	}

	@Test // DATAREST-160
	public void hasValueObjectEqualsSemantics() {

		ETag one = ETag.from("1");
		ETag two = ETag.from("2");
		ETag nullETag = ETag.from((String) null);

		assertThat(one.equals(one)).isTrue();
		assertThat(one.equals(two)).isFalse();
		assertThat(two.equals(one)).isFalse();
		assertThat(nullETag.equals(one)).isFalse();
		assertThat(one.equals(two)).isFalse();
		assertThat(one.equals("")).isFalse();
	}


	@Test // DATAREST-160
	public void noETagReturnsNullForToString() {
		assertThat(ETag.NO_ETAG.toString()).isNull();
	}

	@Test // DATAREST-160
	public void noETagDoesNotRejectVerification() {
		boolean r = ETag.NO_ETAG.equals(ETag.from(new SimpleTestEntity()));
		assertThat(r).isFalse();
	}

	@Test // DATAREST-160
	public void verificationDoesNotRejectNullEntity() {
		ETag.from("5").equals(null);
	}

	@Test // DATAREST-160
	public void stripsTrailingAndLeadingQuotesOnCreation() {

		assertThat(ETag.from("\"1\"")).isEqualTo(ETag.from("1"));
		assertThat(ETag.from("\"\"1\"\"")).isEqualTo(ETag.from("1"));
	}

	@Test // DATAREST-160
	public void addsETagToHeadersIfNotNoETag() {

		HttpHeaders headers = ETag.from("1").addTo(new HttpHeaders());
		assertThat(headers.getETag()).isNotNull();
	}

	@Test // DATAREST-160
	public void doesNotAddHeaderForNoETag() {

		HttpHeaders headers = ETag.NO_ETAG.addTo(new HttpHeaders());

		assertThat(headers.containsKey("ETag")).isFalse();
	}

	// tag::versioned-sample[]
	public class Sample {

		@Version Long version; // <1>

		Sample(Long version) {
			this.version = version;
		}
	}
	// end::versioned-sample[]

	public class SampleWithoutVersion {}
}