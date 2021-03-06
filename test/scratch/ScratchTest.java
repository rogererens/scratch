/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scratch;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ScratchTest {
	@Test public void creatingScratches() {
		Scratch scratch = Scratch.createFrom("scratch.txt");
		assertThat(scratch.name, equalTo("scratch"));
		assertThat(scratch.extension, equalTo("txt"));

		scratch = Scratch.createFrom("&scratch.txt");
		assertThat(scratch.name, equalTo("scratch"));
		assertThat(scratch.extension, equalTo("txt"));

		scratch = Scratch.createFrom("scratch.t&xt");
		assertThat(scratch.name, equalTo("scratch"));
		assertThat(scratch.extension, equalTo("txt"));

		scratch = Scratch.createFrom("scratch");
		assertThat(scratch.name, equalTo("scratch"));
		assertThat(scratch.extension, equalTo(""));
	}
}
