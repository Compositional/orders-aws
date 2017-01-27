package works.weave

import javax.inject.Inject
import scala.annotation.meta.beanSetter
import scala.beans.BeanProperty

package object spring {

  /** Creates a spring injection setter.
    */
  type inject = Inject @beanSetter

}
